$(document).ready(function() {

    var testid;




    // CHECKING ID VERIF
    $('#reg-user-id').keyup(function() {

        var content1 = {
            user_id: $("#reg-user-id").val()
        }
        console.log(content1)

        if (validateId()) {

            $.ajax({
                type: 'POST',
                contentType: "application/json",
                url: '/duplicateId',
                data: JSON.stringify(content1),
                dataType: "json",
                success: function(response) {

                    if (response.code == '200') {
                        $(".status-11").html("<font color=green size=0.07px><br>사용 가능한 이아디입니다</font>");

                        testid = 1;

                    } else {
                        $(".status-11").html("<font color=red size=0.07px><br>중북된 아이디입니다</font>");

                        testid = 0;
                    }

                }
            });

            $(".status-1").html("<font color=green size=0.07px>3~12자의 영문, 숫자만 사용 가능</font>");

        } else {
            $(".status-1").html("<font color=red size=0.07px>1~12자의 영문, 숫자 조합만 사용 가능</font>");
            $(".status-11").html("<font color=red size=0.07px><br>중북된 아이디입니다</font>");


        }


    });

    function validateId() {
        var uid = $('#reg-user-id').val();
        var pattern = /^[A-Za-z0-9]*$/;

        if (uid.length >= 3 && uid.length <= 12 && pattern.test(uid)) {
            return true;
        } else {
            return false;
        }
    }




    // CHECKING PASSWORD VERIF
    $('#reg-user-password').keyup(function() {

        if (validatePass()) {
            $(".status-2").html("<font color=green size=2px>8~15자의 영문 소문자, 대문자, 숫자만 사용 가능</font>");


        } else {
            $(".status-2").html("<font color=red size=2px>8~15자의 영문 소문자, 대문자, 숫자만 사용 가능</font>");


        }

    });



    function validatePass() {
        var ups = $('#reg-user-password').val();

        if (ups.length >= 8 && ups.length <= 15 && /[a-z]/.test(ups) && /[A-Z]/.test(ups) && /[0-9]/.test(ups)) {
            return true;
        } else {
            return false;
        }
    }






    $("#customer-form-2").submit(function(event) {
        // Prevent the form from submitting via the browser.

        if (testid >= 1 && validatePass()) {

            // PREPARE FORM DATA
            var formData = {
                user_id: $("#reg-user-id").val(),
                user_pw: $("#reg-user-password").val(),
                user_name: $("#reg-username").val(),

            }
            console.log(formData);
            // DO POST
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/signUp",
                data: JSON.stringify(formData),
                dataType: 'json',
                success: function(result) {
                    console.log(result);
                    if (result.code == '200') {


                        alert("Sign Up is Complete.");

                        resetData();

                    } else {

                        alert(result.message);
                    }

                    function resetData() {
                        $("#reg-user-id").val("");
                        $("#reg-user-password").val("");
                        $("#reg-username").val("");
                    }
                }


            });


            // function resetData(){
            //   $("#user-id").val("");
            //   $("#user-password").val("");
            //   $("#username").val("");
            //   $("#user-email").val("");
            // }


        } else {
            console.log(testid);

            event.preventDefault();
            alert("아이디 / 비밀번호을 잘 확인하여 등록해 주십시요.");


        }


    });




    $("#customer-form-1").submit(function() {

        var username = $("#user-id").val();
        sessionStorage.setItem('id', username);



    });

    $("#exit").click(function() {

        sessionStorage.clear();
        sessionStorage.removeItem('reg-user-id');
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

    $("#requestNewPassword").on("click", function() {
        var userID = $("#user-id").val().trim();

        if (userID == "") {
            $('.warning-msg').html("사용자 ID 가 유효하지 않습니다.");
            $(".warning-msg").animate({ opacity: 1 }, 500, function() {
                $('.warning-msg').css('visibility', 'visible');
                $(".warning-msg").animate({ opacity: 0 }, 3000, function() {
                    $('.warning-msg').css('visibility', 'hidden');
                });
            });
        } else {
            $.ajax({
                type: 'POST',
                url: '/user/emailResetPasswordRequest?user_id=' + userID,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function(response) {
                    if (response.code == '200') {
                        if (response.status != null) {
                            $('.success-msg').html("비밀번호 초기화 링크가 사용자ID + 한품 이메일을 통해 발송되었습니다. \
                                <br/>해당 링크는 5분 이내 만료될 예정입니다. ");
                            $('.warning-msg').html(response.status);
                            $('.success-msg, .warning-msg').css('visibility', 'visible');
                            $('.success-msg, .warning-msg').fadeIn(100).delay(5000).fadeOut(500);
                        } else {
                            $('.success-msg').html("비밀번호 초기화 링크가 사용자ID + 한품 이메일을 통해 발송되었습니다. \
                            <br/>해당 링크는 5분 이내 만료될 예정입니다. ");
                            $('.success-msg').css('visibility', 'visible');
                            $('.success-msg').fadeIn(100).delay(5000).fadeOut(500);
                        }

                        var object = response.object;
                        var param = {
                            "user_id": object.user_id,
                            "cert_1": object.cert_1,
                            "cert_2": object.cert_2
                        };

                        $.ajax({
                            type: 'POST',
                            url: '/user/sendFindPWEmail',
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            data: JSON.stringify(param),
                            success: function(response) {
                                console.log(response);
                            }
                        });
                    } else if (response.code == '403') {
                        $('.warning-msg').html(response.message);
                        $('.warning-msg').css('visibility', 'visible');
                        $('.warning-msg').fadeIn(100).delay(5000).fadeOut(500);
                    } else if (response.code == '404' || response.code == '400') {
                        $('.warning-msg').html("사용자 ID 가 유효하지 않습니다.");
                        $('.warning-msg').css('visibility', 'visible');
                        $('.warning-msg').fadeIn(100).delay(5000).fadeOut(500);

                    } else if (response.code == "500") {
                        console.log("123123");
                    } else {
                        alert('An error has occurred.');
                    }
                }
            });
        }
    });

    $("#submitResetPassword").on("click", function() {
        var new_pass = $("#newPassword").val().trim();
        var re_enter_pass = $("#reEnterPassword").val().trim();
        var user_id = getUrlParameter('user_id');

        var formData = {
            user_id: user_id,
            user_pw: new_pass
        };

        console.log(formData);

        if (new_pass == "") {
            $("#newPassword").css("border", "2px solid #ff3b2c");
        } else if (re_enter_pass == "") {
            $("#reEnterPassword").css("border", "2px solid #ff3b2c");
        } else {
            $("#newPassword").css("border", "2px solid #ddd");
            $("#reEnterPassword").css("border", "2px solid #ddd");
        }

        if (new_pass != re_enter_pass) {
            $(".warning-msg").text("비밀번호가 일치하지 않습니다.");
            $(".warning-msg").animate({ opacity: 1 }, 500, function() {
                $('.warning-msg').css('visibility', 'visible');
                $(".warning-msg").animate({ opacity: 0 }, 15000, function() {
                    $('.warning-msg').css('visibility', 'hidden');
                });
            });
            $("#newPassword").css("border", "2px solid #ff3b2c");
            $("#reEnterPassword").css("border", "2px solid #ff3b2c");
        } else {
            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '/user/changePassword',
                data: JSON.stringify(formData),
                dataType: 'json',
                encode: true,
                success: function(response) {
                    if (response.code == '200') {
                        sendNotification(user_id, "", "change_password");
                        window.location.href = '/signIn';
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

    $("#newPassword, #reEnterPassword").keyup(function() {
        var input = $(this);
        if (input.val() == "") {
            input.css("border", "2px solid #ff3b2c");
        } else {
            input.css("border", "1px solid #ddd");
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