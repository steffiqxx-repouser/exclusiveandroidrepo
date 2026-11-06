
import express from "express";
import OpenAI from "openai";
import dotenv from "dotenv";
import rateLimit from "express-rate-limit";

dotenv.config();

const app = express();

app.use(express.json({ limit: "10kb" }));

const limiter = rateLimit({
    windowMs: 60 * 1000,
    limit: 20,
    standardHeaders: true,
    legacyHeaders: false
});

app.use("/v1/command", limiter);

const openai = new OpenAI({
    apiKey: process.env.OPENAI_API_KEY
});

const MODEL = process.env.OPENAI_MODEL || "gpt-4.1-mini";

// Only these operations can be returned to Android.
const ALLOWED_ACTIONS = new Set([
    "SET_ORIENTATION",
    "SET_THEME",
    "SET_TEXT_SIZE",
    "OPEN_CALCULATOR",
    "OPEN_SETTINGS",
    "SHOW_DATE"
]);

const SYSTEM_PROMPT = `
You are the natural language interpreter
for an Android application.

Your responsibility is to understand user
instructions and convert them into structured JSON.

You NEVER execute Android operations.

Supported Android actions:

1. SET_ORIENTATION
   parameters:
   {"orientation":"PORTRAIT"}
   {"orientation":"LANDSCAPE"}
   {"orientation":"AUTO"}

2. SET_THEME
   parameters:
   {"theme":"DARK"}
   {"theme":"LIGHT"}

3. SET_TEXT_SIZE
   parameters:
   {"sizeSp":24}

4. OPEN_CALCULATOR
   parameters: {}

5. OPEN_SETTINGS
   parameters: {}

6. SHOW_DATE
   parameters: {}

For information requests, set searchQuery
to a useful search query.

Examples:

User: Rotate the screen horizontally.

{
  "actions": [
    {
      "type": "SET_ORIENTATION",
      "parameters": {
        "orientation": "LANDSCAPE"
      }
    }
  ],
  "searchQuery": null,
  "message": null
}

User: Make the screen dark and increase text size to 24.

{
  "actions": [
    {
      "type": "SET_THEME",
      "parameters": {
        "theme": "DARK"
      }
    },
    {
      "type": "SET_TEXT_SIZE",
      "parameters": {
        "sizeSp": 24
      }
    }
  ],
  "searchQuery": null,
  "message": null
}

User: Find the most deserted place on Earth.

{
  "actions": [],
  "searchQuery": "Most deserted and uninhabited place on Earth",
  "message": null
}

User: Explain how garbage collection works in Java.

{
  "actions": [],
  "searchQuery": "How Java garbage collection works",
  "message": null
}

User: Turn on Bluetooth.

{
  "actions": [],
  "searchQuery": null,
  "message": "Direct Bluetooth control is not supported."
}

Rules:

- Return only a valid JSON object.
- Never invent unsupported Android capabilities.
- Do not return shell commands, source code or executable instructions.
- Never request unrestricted Android permissions.
- Multiple supported actions can be returned together.
- Use searchQuery for information and research requests.
- If a request is ambiguous, use message to ask for clarification.
- Never interpret an information request as permission
  to modify device settings.

Required JSON structure:

{
  "actions": [],
  "searchQuery": null,
  "message": null
}
`;

function validateActions(actions) {

    if (!Array.isArray(actions)) {
        return [];
    }

    return actions
        .slice(0, 5)
        .filter(action =>
            action &&
            ALLOWED_ACTIONS.has(action.type)
        )
        .map(action => {

            const p = action.parameters || {};

            switch (action.type) {

                case "SET_ORIENTATION":

                    if (![
                        "PORTRAIT",
                        "LANDSCAPE",
                        "AUTO"
                    ].includes(p.orientation)) {
                        return null;
                    }

                    return {
                        type: action.type,
                        parameters: {
                            orientation: p.orientation
                        }
                    };

                case "SET_THEME":

                    if (![
                        "DARK",
                        "LIGHT"
                    ].includes(p.theme)) {
                        return null;
                    }

                    return {
                        type: action.type,
                        parameters: {
                            theme: p.theme
                        }
                    };

                case "SET_TEXT_SIZE": {

                    const size = Number(p.sizeSp);

                    if (!Number.isInteger(size) ||
                        size < 12 ||
                        size > 32) {
                        return null;
                    }

                    return {
                        type: action.type,
                        parameters: {
                            sizeSp: size
                        }
                    };
                }

                default:

                    return {
                        type: action.type,
                        parameters: {}
                    };
            }
        })
        .filter(Boolean);
}

async function interpretCommand(text) {

    const response = await openai.responses.create({

        model: MODEL,

        instructions: SYSTEM_PROMPT,

        input: text,

        text: {
            format: {
                type: "json_object"
            }
        }
    });

    const parsed = JSON.parse(response.output_text);

    const actions = validateActions(parsed.actions);

    let answer = parsed.message || null;

    // Perform a real web search for information requests.
    if (typeof parsed.searchQuery === "string" &&
        parsed.searchQuery.trim()) {

        const searchResponse =
            await openai.responses.create({

                model: MODEL,

                tools: [
                    {
                        type: "web_search"
                    }
                ],

                input:
                    "Research this request using web search. " +
                    "Give a factual, concise answer and explain " +
                    "any uncertainty: " +
                    parsed.searchQuery
            });

        answer = searchResponse.output_text;
    }

    return {
        actions,
        answer
    };
}

app.get("/health", (req, res) => {
    res.json({
        status: "UP",
        application: "Natural Command"
    });
});

app.post("/v1/command", async (req, res) => {

    try {

        const { text } = req.body || {};

        if (typeof text !== "string" ||
            !text.trim() ||
            text.length > 2000) {

            return res.status(400).json({
                error: "Invalid command"
            });
        }

        const result =
            await interpretCommand(text.trim());

        return res.json(result);

    } catch (error) {

        console.error("Command processing failed:", error);

        return res.status(500).json({
            error: "Unable to process command"
        });
    }
});

const PORT = process.env.PORT || 8080;

app.listen(PORT, "0.0.0.0", () => {

    console.log(
        `Natural Command backend running on port ${PORT}`
    );

});
