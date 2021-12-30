var userID, emailAdd;

$(document).ready(function() {
    userID = $("span#userID").text().trim();

    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: '/user/getUserById?user_id=' + userID,
        success: function(response) {
            const jsonObject = response.object;
            $("#firstName").val(jsonObject.user_name);
            $("#lastName").val(jsonObject.last_name);
            $("#phoneNumber").val(jsonObject.phone_number);
            $("#emailAddress").val(jsonObject.user_email);

            emailAdd = jsonObject.user_email;
        },
        error: function(response) {
            console.log(response);
        }
    });

    $(".sidebar ul li").click(function(e) {
        e.preventDefault();
        var content = $(this).attr("rel");
        $(this).siblings().removeClass("active");
        $(this).addClass("active");
        $('#' + content).addClass("active");
        $('#' + content).siblings().removeClass("active");
    });

    // eye icon reset password
    $(".eye").on("click", function() {
        if ($(this).hasClass('bi-eye-slash')) {
            $(this).prev('.password').get(0).type = 'text';
            $(this).removeClass('bi-eye-slash');
            $(this).addClass('bi-eye');
        } else {
            $(this).prev('.password').get(0).type = 'password';
            $(this).addClass('bi-eye-slash');
            $(this).removeClass('bi-eye');
        }
    });

    $("#submitDetails").on("click", function() {
        var user_name = $("#firstName").val().trim();
        var last_name = $("#lastName").val().trim();
        var phone_number = $("#phoneNumber").val().trim();

        var formData = {
            user_id: userID,
            user_name: user_name,
            last_name: last_name,
            phone_number: phone_number
        };

        console.log(formData);

        if (user_name == "") {
            $("#firstName").css("border", "2px solid #ff3b2c");
        } else if (last_name == "") {
            $("#lastName").css("border", "2px solid #ff3b2c");
        } else if (phone_number == "") {
            $("#phoneNumber").css("border", "2px solid #ff3b2c");
        } else {
            $("#firstName, #lastName, #phoneNumber").css("border", "2px solid #ddd");
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '/user/updateUserInfo',
                data: JSON.stringify(formData),
                dataType: 'json',
                encode: true,
                success: function(response) {
                    if (response.code == '200') {
                        $('#submitDetails').next(".notification-msg-container").fadeIn('500', function() {
                            $(this).find(".notification-msg").addClass("success");
                            $(this).find(".notification-msg").html("<i class='fas fa-check-circle'></i>개인 정보가 성공적으로 변경 되었습니다.");
                        }).delay(3000).fadeOut();
                        sendNotification(userID, emailAdd, "change_user_info");
                    } else if (response.code == '403') {
                        window.location.href = '/signIn';
                    } else {
                        alert('An error has occurred.');
                    }
                },
                error: function(response) {
                    console.log(response);
                }
            });
        }
    });

    $("#firstName, #lastName, #phoneNumber, #emailAddress, #reEnterEmailAddress, #userPassword, #reEnterPassword").keyup(function() {
        var input = $(this);
        if (input.val() == "") {
            input.css("border", "2px solid #ff3b2c");
        } else {
            input.css("border", "1px solid #ddd");
        }
    });

    $("#submitEmailAddress").on("click", function() {
        var user_email = $("#emailAddress").val().trim();
        var re_enter_email = $("#reEnterEmailAddress").val().trim();

        var formData = {
            user_id: userID,
            user_email: user_email
        };

        if (user_email == "") {
            $("#emailAddress").css("border", "2px solid #ff3b2c");
        } else if (re_enter_email == "") {
            $("#reEnterEmailAddress").css("border", "2px solid #ff3b2c");
        } else {
            $("#emailAddress, #reEnterEmailAddress").css("border", "2px solid #ddd");
        }

        if (user_email != re_enter_email) {
            $('#submitEmailAddress').next(".notification-msg-container").fadeIn('1000', function() {
                $(this).find(".notification-msg").removeClass("success").addClass("warning");
                $(this).find(".notification-msg").html("<i class='fas fa-exclamation-circle'></i>이메일 주소가 일치하지 않습니다.");
            }).delay(3000).fadeOut(function() {
                $(this).find(".notification-msg").html("");
                $(this).find(".notification-msg").removeClass("success").removeClass("warning");
            });

            $("#emailAddress, #reEnterEmailAddress").css("border", "2px solid #ff3b2c");
        } else if (!isHanpoomEmail(user_email)) {
            $('#submitEmailAddress').next(".notification-msg-container").fadeIn('1000', function() {
                $(this).find(".notification-msg").removeClass("success").addClass("warning");
                $(this).find(".notification-msg").html("").html("<i class='fas fa-exclamation-circle'></i>@hanpoom.com 도메인을 가진 이메일만 사용 가능 합니다.");
            }).delay(3000).fadeOut(function() {
                $(this).find(".notification-msg").html("");
                $(this).find(".notification-msg").removeClass("success").removeClass("warning");
            });
        } else {
            $("#emailAddress, #reEnterEmailAddress").css("border", "2px solid #ddd");
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '/user/updateUserEmail',
                data: JSON.stringify(formData),
                dataType: 'json',
                encode: true,
                success: function(response) {
                    if (response.code == '200') {
                        $('#submitEmailAddress').next(".notification-msg-container").fadeIn('1000', function() {
                            $(this).find(".notification-msg").removeClass("warning").addClass("success");
                            $(this).find(".notification-msg").html("<i class='fas fa-check-circle'></i>이메일이 성공적으로 변경 되었습니다.");
                        }).delay(3000).fadeOut(function() {
                            $(this).find(".notification-msg").html("");
                            $(this).find(".notification-msg").removeClass("success").removeClass("warning");
                        });
                        sendNotification(userID, emailAdd, "change_email");
                    } else if (response.code == '403') {
                        window.location.href = '/signIn';
                    } else if (response.code == '400') {
                        $('#submitEmailAddress').next(".notification-msg-container").fadeIn('1000', function() {
                            $(this).find(".notification-msg").removeClass("success").addClass("warning");
                            $(this).find(".notification-msg").html("<i class='fas fa-exclamation-circle'></i>해당 이메일은 현재 다른 사용자에 의해 사용되고 있습니다.");
                        }).delay(3000).fadeOut(function() {
                            $(this).find(".notification-msg").html("");
                            $(this).find(".notification-msg").removeClass("success").removeClass("warning");
                        });
                    } else {
                        alert('An error has occurred.');
                        $(".loading").css("display", "none");
                    }
                },
                error: function(response) {
                    console.log(response);
                }
            });
        }
    });

    $("#submitPassword").on("click", function() {
        var old_pw = $("#oldPassword").val().trim();
        var user_pw = $("#userPassword").val().trim();
        var re_enter_pass = $("#reEnterPassword").val().trim();

        var formData = {
            user_id: userID,
            old_password: old_pw,
            new_password: user_pw
        };

        console.log(formData);

        if (old_pw == "") {
            $("#oldPassword").css("border", "2px solid #ff3b2c");
        } else if (user_pw == "") {
            $("#reEnterPassword").css("border", "2px solid #ff3b2c");
        } else if (re_enter_pass == "") {
            $("#userPassword").css("border", "2px solid #ff3b2c");
        } else {
            $("#userPassword, #reEnterPassword, #oldPassword").css("border", "2px solid #ddd");
        }

        if (user_pw != re_enter_pass) {
            $('#submitPassword').next(".notification-msg-container").fadeIn('1000', function() {
                $(this).find(".notification-msg").removeClass("success").addClass("warning");
                $(this).find(".notification-msg").html("<i class='fas fa-exclamation-circle'></i>비밀번호가 일치하지 않습니다.");
            }).delay(3000).fadeOut(function() {
                $(this).find(".notification-msg").html("");
                $(this).find(".notification-msg").removeClass("success").removeClass("warning");
            });

            $("#userPassword, #reEnterPassword, #oldPassword").css("border", "2px solid #ff3b2c");
        } else {
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '/user/updateUserPassword',
                data: JSON.stringify(formData),
                dataType: 'json',
                encode: true,
                success: function(response) {
                    if (response.code == '200') {
                        $('#submitPassword').next(".notification-msg-container").fadeIn('1000', function() {
                            $(this).find(".notification-msg").removeClass("warning").addClass("success");
                            $(this).find(".notification-msg").html("<i class='fas fa-check-circle'></i>비밀번호가 성공적으로 번경 되었습니다.");
                        }).delay(3000).fadeOut(function() {
                            $(this).find(".notification-msg").html("");
                            $(this).find(".notification-msg").removeClass("success").removeClass("warning");
                        });
                        sendNotification(userID, emailAdd, "change_password");
                    } else if (response.code == '403') {
                        window.location.href = '/signIn';
                    } else if (response.code == '400') {
                        $('#submitPassword').next(".notification-msg-container").fadeIn('1000', function() {
                            $(this).find(".notification-msg").removeClass("success").addClass("warning");
                            $(this).find(".notification-msg").html("<i class='fas fa-exclamation-circle'></i>이전 비밀번호가 일치하지 않습니다.");
                        }).delay(3000).fadeOut(function() {
                            $(this).find(".notification-msg").html("");
                            $(this).find(".notification-msg").removeClass("success").removeClass("warning");
                        });
                    } else {
                        alert('An error has occurred.');
                        $(".loading").css("display", "none");
                    }
                },
                error: function(response) {
                    console.log(response);
                }
            });
        }
    });
});

function getUrlParameter(param) {
    var pageUrl = window.location.search.substring(1),
        pageVar = pageUrl.split('&'),
        paramName,
        i;

    for (i = 0; i < pageVar.length; i++) {
        paramName = pageVar[i].split('=');

        if (paramName[0] === param) {
            return typeof paramName[1] === undefined ? true : decodeURIComponent(paramName[1]);
        }
    }
    return false;
}

function isHanpoomEmail(email) {
    var regex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@(hanpoom.com)$/;
    return regex.test(email);
}

function sendNotification(user_id, email, event) {
    $.ajax({
        url: '/user/sendNotification?user_id=' + user_id + '&email=' + email + '&event=' + event,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        success: function() {
            console.log("Email Notification Sent.");
        },
        error: function() {
            console.log("error");
        }
    });
}