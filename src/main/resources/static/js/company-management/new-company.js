$(document).ready(function() { 
    $("#create-company-submit").click(function () {
        validateInputs();
    });
});

function phoneNumber(event) {
    var theEvent = event || window.event;

    if (theEvent.type === 'paste') {
        key = event.clipboardData.getData('text/plain');
    } else {
        var key = theEvent.keyCode || theEvent.which;
        key = String.fromCharCode(key);
    }
    var regex = /[0-9]|\./;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault) theEvent.preventDefault();
    }
}

function isEmailValid(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

function validateInputs() {
    var comp_name = $("#company-name").val();
    var biz_name = $("#business-name").val();
    var biz_no = $("#business-number").val();
    var bank_name = $("#bank-name").val();
    var account_holder = $("#business-owner").val();
    var hanpoom_pic = $("#hanpoom-pic").val();
    var account = $("#bank-account-num").val();

    if (comp_name == "") {
        alert("Company Name must be filled out");
        return false;
    } else if (biz_name == "") {
        alert("Business Name must be filled out");
        return false;
    } else if (biz_no == "") {
        alert("Phone number must be filled out");
        return false;
    } else if (bank_name == "") {
        alert("Bank must be filled out");
        return false;
    } else if (account_holder == "") {
        alert("Business Owner must be filled out");
        return false;
    } else if (hanpoom_pic == "") {
        alert("Hanpoom PIC must be filled out");
        return false;
    } else if (account == "" || account == 0) {
        alert("Account Number is required");
        return false;
    } else {
        submitForm();
        return true;
    }
}

function submitForm() {
    var comp_name = $("#company-name").val();
    var biz_name = $("#business-name").val();
    var biz_no = $("#business-number").val();
    var comp_use_flag = $("#post-status").val();
    var bank_name = $("#bank-name").val();
    var account_holder = $("#business-owner").val();
    var order_type = $("#type").val();
    var hanpoom_pic = $("#hanpoom-pic").val();
    var account = $("#bank-account-num").val();
    var deposit_type = $("#depositType").val();

    var createcompanycontent = {
        comp_name: comp_name,
        biz_name: biz_name,
        biz_no: biz_no,
        comp_use_flag: comp_use_flag,
        bank_name: bank_name,
        account_holder: account_holder,
        order_type: order_type,
        hanpoom_pic: hanpoom_pic,
        account: account,
        transaction_req_date_type: deposit_type
    };

    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/insertCompany',
        data: JSON.stringify(createcompanycontent),
        dataType: "json",
        success: function (response) {
            if (response.code == "200") {
                alert("New Company has been added.");
            } else {
                alert("Failure in adding new record.");
            }
        }
    });
}