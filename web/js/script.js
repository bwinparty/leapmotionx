$(function() {
    var subscription, chatlog;

    subscription = new Object();
    subscription.sock = null;
    subscription.connected = false;

    subscription.init = function() {
        if (subscription.sock === null && subscription.connected === false) {
            subscription.sock = new SockJS("http://10.80.8.19/leap/stream");
            subscription.sock.onopen = function() {
                subscription.connected = true;
                console.log('open');
                chatlog('connection opened');
            };

            subscription.sock.onmessage = function(e) {
                //console.log('message', e.data);
                obj = JSON.parse(e.data);
                frame = new Leap.Frame(obj);
                chatlog(e.data);
                $('#hands').text(frame.hands.length);
                $('#fingers').text(frame.pointables.length);
            };

            subscription.sock.onclose = function() {
                if(subscription.connected === true) {
                    setTimeout(function() {
                        subscription.reconnect();
                    }, 5000);
                    chatlog('connection closed');
                    console.log('close');
                }
                subscription.connected = false;
                subscription.stop();
            };
        }
    };

    subscription.send = function(message) {
        if (subscription.sock !== null && subscription.connected === true) {
            subscription.sock.send(JSON.stringify(message));
        }
    };

    subscription.stop = function() {
        if (subscription.sock !== null) {
            subscription.sock.close();
        }
        subscription.sock = null;
    };

    subscription.reconnect = function() {
        if (subscription.connected === false) {
            subscription.init();
            setTimeout(function() {
                subscription.reconnect();
            }, 5000);
        }
    };
    chatlog = function(data) {
        $("#chat").prepend('<li>' + data + '</li>');
        $("#chat li:gt(19)").fadeOut(500, function() {
            $(this).remove();
        });

    };
    subscription.init();
});
