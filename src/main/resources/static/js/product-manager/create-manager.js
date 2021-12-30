$(document).ready(function() {
    $("#manager-submit").click(function () {
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
    var mgr_name = $("#manager-name").val().trim();
    var mgr_rank = $("#manager-rank").val().trim();
    var mgr_phone_no = $("#manager-contact").val().trim();
    var mgr_email = $("#manager-email").val().trim();
    var comp_id = $("#mgr-company").val();

    if (mgr_name == "") {
        alert("Name must be filled out");
        return false;
    } else if (mgr_rank == "") {
        alert("Rank must be filled out");
        return false;
    } else if (mgr_phone_no == "") {
        alert("Phone number must be filled out");
        return false;
    } else if (mgr_email == "") {
        alert("Email must be filled out");
        return false;
    } else if (!isEmailValid(mgr_email)) {
        alert("Please enter a valid email.");
        return false;
    } else if (comp_id == "" || comp_id == 0) {
        alert("Please select company");
        return false;
    } else {
        submitForm();
        return true;
    }
}

function submitForm() {
    var mgr_name = $("#manager-name").val();
    var mgr_rank = $("#manager-rank").val();
    var mgr_phone_no = $("#manager-contact").val();
    var mgr_email = $("#manager-email").val();
    var mgr_use_flag = $("#post-status").val();
    var comp_id = $("#mgr-company").val();

    var managercontent = {
        mgr_name: mgr_name,
        mgr_rank: mgr_rank,
        mgr_phone_no: mgr_phone_no,
        mgr_email: mgr_email,
        mgr_use_flag: mgr_use_flag,
        comp_id: comp_id
    };
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/insertProductManager',
        data: JSON.stringify(managercontent),
        dataType: "json",
        success: function (response) {
            if (response.code == "200") {
                alert("New Manager Has Been Added.");
            } else {
                alert("Failure in adding new record.");
            }

        }
    });
}