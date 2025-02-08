package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final String JSON_URL = "https://raw.githubusercontent.com/jxroot/webview_inject/refs/heads/main/app.json"; // JSON file URL
    private JSONObject scripts; // Store JavaScript rules
    private boolean isPersistenceEnabled = false; // Persistent script toggle
    private ArrayList<String> persistentScripts = new ArrayList<>(); // Persistent JavaScript list

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize WebView and set its settings
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  // Enable JavaScript
        webSettings.setDomStorageEnabled(true);  // Enable DOM storage (important for some JS)
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // Disable caching

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("WebView", "Page Finished: " + url);
                injectJavaScript(url); // Inject JavaScript for the current site
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        // Fetch JSON file and load the webpage
        fetchJsonAndLoadPage();
    }

    private void fetchJsonAndLoadPage() {
        new Thread(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                scripts = jsonObject.getJSONObject("websites"); // Store JavaScript rules

                // Handle persistent scripts
                if (jsonObject.has("persistent")) {
                    JSONObject persistentSection = jsonObject.getJSONObject("persistent");
                    isPersistenceEnabled = persistentSection.optBoolean("status", false);
                    if (isPersistenceEnabled && persistentSection.has("scripts")) {
                        JSONArray persistentArray = persistentSection.getJSONArray("scripts");
                        for (int i = 0; i < persistentArray.length(); i++) {
                            persistentScripts.add(persistentArray.getString(i));
                        }
                    }
                }

                // Load the first URL from JSON (or default to example.com)
                String firstUrl = jsonObject.has("url") ? jsonObject.getString("url") : "https://example.com";
                runOnUiThread(() -> webView.loadUrl(firstUrl));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void injectJavaScript(String url) {
        try {
            String domain = new URL(url).getHost(); // Extract domain from URL
            JSONObject jsRule = findMatchingScriptRule(domain); // Find matching script rule

            if (jsRule != null) {
                // Extract scripts for this specific website
                JSONArray jsScripts = jsRule.getJSONArray("scripts");

                // Inject website-specific JavaScript
                for (int i = 0; i < jsScripts.length(); i++) {
                    String jsCode = jsScripts.getString(i);
                    Log.d("WebView", "Injecting JS: " + jsCode); // Debugging line
                    webView.evaluateJavascript(jsCode, value -> {
                        Log.d("WebView", "JS evaluated: " + value); // Log the result of JS execution
                    });
                }
            }

            // Inject persistent scripts only if persistence is enabled
            if (isPersistenceEnabled) {
                for (String script : persistentScripts) {
                    Log.d("WebView", "Injecting persistent JS: " + script); // Debugging line
                    webView.evaluateJavascript(script, value -> {
                        Log.d("WebView", "Persistent JS evaluated: " + value); // Log persistent JS execution
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject findMatchingScriptRule(String domain) {
        try {
            // Step 1: Check for exact domain match
            if (scripts.has(domain)) {
                return scripts.getJSONObject(domain);
            }

            // Step 2: Check for wildcard match "*.domain.com"
            Iterator<String> keys = scripts.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.startsWith("*.")) {
                    String wildcardDomain = key.substring(2); // Remove "*." prefix
                    // Wildcard domain should match the end of the current domain (i.e., subdomains)
                    if (domain.endsWith(wildcardDomain)) {
                        return scripts.getJSONObject(key);
                    }
                }
            }

            // Step 3: Check for regex match "regex:^.*\.example\.com$"
            keys = scripts.keys();  // Re-initialize iterator
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.startsWith("regex:")) {
                    String regexPattern = key.substring(6); // Remove "regex:" prefix
                    if (Pattern.matches(regexPattern, domain)) {
                        return scripts.getJSONObject(key);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();  // Log the exception (you can handle it differently if needed)
        }

        // No match found
        return null;
    }
}
