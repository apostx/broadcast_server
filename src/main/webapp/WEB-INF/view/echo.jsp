<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Echo Test</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script>
        var gameServer = null;

        function connect() {
            showMessage("Connecting...");
            gameServer = new WebSocket("${websocket_url}");

            gameServer.onopen = function() {
                showMessage("Connected");

                var MESSAGE = "Hello World!";

                showMessage("Sent message: " + MESSAGE);
                gameServer.send(MESSAGE);
            };

            gameServer.onmessage = function(e) {
                showMessage("Received message: " + e.data);

                gameServer.close();
            };

            gameServer.onclose = function(e) {
                showMessage("Connection closed ");
            };

            gameServer.onerror = function(e) {
                showMessage("Error!");
            };
        }

        function disconnect(e) {
            gameServer && gameServer.close();
        }

        var stepIndex = 1;

        function showMessage(message) {
            var messagesArea = document.getElementById("messages");
            messagesArea.value = "".concat(messagesArea.value, stepIndex++, ": ", message, "\n");
            messagesArea.scrollTop = messagesArea.scrollHeight;
        }
    </script>
</head>
<body onload="connect();" onunload="disconnect();">
<h1>WebSocket Echo Test</h1>
<textarea style="width: 300px; height: 100px;" id="messages" disabled="true"></textarea>
</body>
</html>
