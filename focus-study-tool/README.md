# Focus Study Tool

একটা distraction-free study desktop app। এতে আছে:

- **Study PDF tab** — PDF ফাইল খুলে page-by-page পড়া যায় (Apache PDFBox দিয়ে render করা)।
- **Study Web tab** — একটা embedded mini-browser, যেখানে শুধু whitelist-এ থাকা site (default: Wikipedia, Google Scholar, Google Docs/Drive) load হবে। বাকি সব (Facebook, YouTube, ইত্যাদি) স্বয়ংক্রিয়ভাবে block হয়ে "Site Blocked" পেজ দেখাবে।
- **Pomodoro timer** — সাইডবারে সবসময় দেখা যায়, focus/break session চালানো যায়, session count track করে।

## কীভাবে সীমাবদ্ধতা কাজ করে

App-এর ভেতরে থাকা `Study Web` ব্রাউজারে URL লোড হওয়ার আগেই তার domain whitelist-এর বিরুদ্ধে check হয়। Whitelist-এ না থাকলে load সাথে সাথে cancel হয়ে যায় এবং একটা blocked পেজ দেখানো হয়। ডান পাশের প্যানেল থেকে whitelist-এ নতুন site যোগ বা বাদ দেওয়া যায় — এটা `~/.focus-study-tool/whitelist.txt` ফাইলে সেভ থাকে।

**সীমাবদ্ধতা যা জানা দরকার:** এটা app-level block, OS-level নয়। মানে student চাইলে app বন্ধ করে সাধারণ browser (Chrome/Edge) খুলে যেকোনো সাইটে যেতে পারবে — কারণ এই app অন্য কোনো software-কে touch করে না। এটা মূলত willing student-এর জন্য "একটা জায়গায় সব থাকুক, বারবার অন্য tab এ যাওয়ার প্রলোভন কমাই" — এই উদ্দেশ্যে বানানো। সত্যিকারের OS-level lock দরকার হলে (parental-control style), সেটার জন্য আলাদা approach লাগবে (hosts file / firewall rule, admin permission সহ) — চাইলে সেটাও পরে যোগ করে দিতে পারি।

## চালানোর জন্য প্রয়োজন

- Java 17+ (JDK, শুধু JRE না)
- Apache Maven

## চালানোর পদ্ধতি

```bash
cd focus-study-tool
mvn clean javafx:run
```

প্রথমবার চালাতে Maven internet থেকে JavaFX ও PDFBox dependency download করবে (pom.xml-এ দেওয়া আছে), তাই প্রথমবার internet connection লাগবে।

## Fullscreen / harder-to-leave mode (optional)

`Main.java`-তে নিচের দুই লাইন uncomment করলে app খোলার সাথে সাথে fullscreen হয়ে যাবে এবং exit hint দেখাবে না — এতে casual ভাবে অন্য জায়গায় চলে যাওয়া একটু কঠিন হয়:

```java
stage.setFullScreen(true);
stage.setFullScreenExitHint("");
```

## প্রজেক্ট গঠন

```
focus-study-tool/
├── pom.xml
└── src/main/java/com/focustool/
    ├── Main.java                 (entry point, layout)
    ├── ui/
    │   ├── PomodoroPane.java     (timer sidebar)
    │   ├── PdfViewerPane.java    (PDF reading tab)
    │   └── BrowserPane.java      (whitelisted mini-browser tab)
    └── util/
        └── WhitelistManager.java (allowed-domain logic + persistence)
```

## ভবিষ্যতে যোগ করা যায় এমন feature

- Session history/statistics (কোনদিন কতক্ষণ পড়াশোনা করলেন)
- Multiple whitelist profile (যেমন "Math class" বনাম "General reading")
- OS-level site blocking (hosts file বা firewall rule দিয়ে, admin permission সহ)
- App switching lock করা (window focus হারালে warning)
