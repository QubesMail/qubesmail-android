import json
import re
import os

with open("invoke_writer.json", "r", encoding="utf-8") as f:
    lines = f.readlines()

data = json.loads(lines[1])
prompt = data["tool_calls"][0]["args"]["Subagents"][0]["Prompt"]

files = re.split(r"## FILE \d+: ", prompt)[1:]
for file_section in files:
    lines_section = file_section.split("\n")
    path = lines_section[0].strip()
    
    # We only care about internal module files that were deleted
    if path.startswith("feature/ai/internal/src/commonMain/kotlin"):
        new_path = path.replace("src/commonMain/kotlin", "src/main/kotlin")
        
        # Extract code block
        match = re.search(r"```kotlin\n(.*?)```", file_section, re.DOTALL)
        if match:
            code = match.group(1).strip() + "\n"
            
            # create dirs
            os.makedirs(os.path.dirname(new_path), exist_ok=True)
            
            with open(new_path, "w", encoding="utf-8") as out:
                out.write(code)
            print(f"Restored: {new_path}")
            
    # Also restore tests just in case I deleted them or if they were in commonMain
    if path.startswith("feature/ai/internal/src/test/kotlin"):
        match = re.search(r"```kotlin\n(.*?)```", file_section, re.DOTALL)
        if match:
            code = match.group(1).strip() + "\n"
            os.makedirs(os.path.dirname(path), exist_ok=True)
            with open(path, "w", encoding="utf-8") as out:
                out.write(code)
            print(f"Restored test: {path}")
