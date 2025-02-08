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
   - ✅ **Persistent scripts**  (optional: run on all pages if enabled)
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
            "alert('This script runs on ALL pages!');"
        ],
        "status": false
    }
}
