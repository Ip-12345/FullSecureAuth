# 🔐 IPVAULT

> **Secure the Future of Sign-In**

IpVault is a lightweight yet powerful authentication system designed to bring enterprise-grade security to your apps without the overhead.

## 🧪 Getting Started

# 🔒 ZAP Security Scan Report – IpVault App

This document summarizes the results of an automated security assessment performed using **OWASP ZAP (v2.16.1)** for the **Authify application**, a full-stack web app using:

- 🔹 React (Frontend) – `localhost:5173`
- 🔹 Spring Boot (Backend) – `localhost:8080`

---

## ✅ Scan Setup

| Parameter          | Value                         |
|--------------------|-------------------------------|
| Scanner Tool       | OWASP ZAP 2.16.1              |
| Proxy Port         | `localhost:9000`              |
| Scan Type          | Manual Browsing + Active Scan |
| Auth Method        | JWT Token (manually captured) |
| Date of Scan       | `25 June 2025`                |

---

## 📊 Summary of Results

| Risk Level     | Number of Alerts |
|----------------|------------------|
| 🔴 High         | 0                |
| 🟠 Medium       | 0                |
| 🟡 Low          | 0                |
| 🔵 Informational | 1                |
| ✅ Total Issues | 1 (Informational only) |

---

## ⚠️ Alert Details

| Type               | Risk Level    | Confidence | Endpoint Tested            |
|--------------------|---------------|------------|-----------------------------|
| `User Agent Fuzzer` | Informational | Medium     | `GET /api/v1.0` on backend  |

> This alert was generated by ZAP trying various `User-Agent` headers to probe responses. No unusual behavior was detected. This is **not a security issue**, just a generic observation from a fuzzer module.

---

## ✅ Conclusion

- No known vulnerabilities were detected in the exposed endpoints of the Authify backend.
- The app appears to correctly handle authenticated and unauthenticated requests.
- This scan confirms basic security hygiene for the API layer.

---

## 📁 Files Included

- `2025-06-25-ZAP-Report.html` — Full ZAP HTML Report
- `README.md` — Summary of the security scan

---

## 🛡️ Recommendations

| Area                | Suggestion                                     |
|---------------------|------------------------------------------------|
| Authentication      | Consider automating login with Context Auth   |
| Headers             | Add Security Headers (CSP, HSTS, etc.)        |
| Scan Depth          | Enable passive scanner add-ons for deeper checks |
| Testing Scope       | Add more complex test cases with input fuzzing |

---

For any future enhancements, include CI-based security testing and SAST tools alongside ZAP.

---


```bash
git clone https://github.com/Ip-12345/FullSecureAuth.git
