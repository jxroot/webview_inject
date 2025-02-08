# webview_inject
Android WebView app dynamically loads a website and injects JavaScript 
## 📂 Example of Matching

| URL Loaded                   | Matched Rule                  | JavaScript Injected        |
|------------------------------|------------------------------|---------------------------|
| `https://api.example.com`    | `*.example.com`              | ✅ Wildcard matched       |
| `https://login.secure123.com` | `regex:^.*\.secure\d+\.com$` | ✅ Regex matched         |
| `https://b.com`              | `b.com`                      | ✅ Exact match           |
| `https://randomsite.com`     | `default`                    | ✅ Default script        |
