$(document).ready(function() {

    var testid;




    // CHECKING ID VERIF
    $('#user-id').keyup(function() {

        var content1 = {
            user_id: $("#user-id").val()
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
            $(".status-1").html("<font color=red size=0.07px>1~12자의 영문, 숫자만 사용 가능</font>");
            $(".status-11").html("<font color=red size=0.07px><br>중북된 아이디입니다</font>");


        }


    });

    function validateId() {
        var uid = $('#user-id').val();
        var pattern = /^[A-Za-z0-9]*$/;

        if (uid.length >= 3 && uid.length <= 12 && pattern.test(uid)) {
            return true;
        } else {
            return false;
        }
    }




    // CHECKING PASSWORD VERIF
    $('#user-password').keyup(function() {

        if (validatePass()) {
            $(".status-2").html("<font color=green size=2px>8~15자의 영문 소문자, 대문자, 숫자만 사용 가능</font>");


        } else {
            $(".status-2").html("<font color=red size=2px>8~15자의 영문 소문자, 대문자, 숫자만 사용 가능</font>");


        }

    });



    function validatePass() {
        var ups = $('#user-password').val();

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
                user_id: $("#user-id").val(),
                user_pw: $("#user-password").val(),
                user_name: $("#username").val(),

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
                        $("#user-id").val("");
                        $("#user-password").val("");
                        $("#username").val("");
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
        sessionStorage.removeItem('user-id');
    });

});