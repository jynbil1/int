let searchParams = new URLSearchParams(window.location.search);

$(document).ready(function () {
    var message = searchParams.get("message");

    if (message != null && message != "") {
        $(".dynamic-message").html('<p class="message-box">' + message + '</p>');
        $(".fa.fa-times-circle").css("margin-top", "0");
    }
});