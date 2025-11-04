# Lab 4 WebSocket — Project Report

## Description of Changes

I completed the `ElizaServerTest.kt` file by implementing the missing `onChat()` test and explaining the required comments (1–6).
The test now connects to the WebSocket server at `/eliza`, sends the message `"I am feeling sad"`, and verifies that the response from the server matches a DOCTOR-style answer related to emotions or beliefs.
I also adjusted the assertions to use an interval check instead of `assertEquals`, because WebSocket communication is asynchronous and the exact number of messages may vary.
Finally, I improved the content validation to accept different valid responses from the ELIZA bot (e.g., messages containing “feel”, “believe”, or “enjoy”).

## Technical Decisions

I decided to use `assertTrue(size in 3..6)` instead of `assertEquals()` to make the test more stable and tolerant of asynchronous message timing.
For checking the content of the messages, I used `list.any { ... }` with flexible keyword matching, since ELIZA’s responses are random and not deterministic.
The client sends `"I am feeling sad"` automatically when the server greeting is received, ensuring that the test runs completely without manual input.

## Learning Outcomes

Through this assignment, I learned how to:

* Test WebSocket connections in Kotlin and Spring Boot.
* Write integration tests for asynchronous communication.
* Handle message timing and variability in WebSocket responses.
* Design tests that are robust and flexible rather than dependent on fixed outputs.

## AI Disclosure

### AI Tools Used

* ChatGPT

### AI-Assisted Work

* The final version of the `onChat()` test and this report were partially reviewed and refined using AI suggestions.
* Approx. **15%** of the work was AI-assisted, mostly for improving wording and structure.
* All AI-generated code was checked, adapted, and tested by me.

### Original Work

* I fully understood how the WebSocket client and server interact, how ELIZA generates responses, and how to verify them properly in tests.
