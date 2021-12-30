$(document).ready(function() {
    var currentPage = "";
    var totalCount = $("#countofproducts").val();
    var totalPages = "";

    totalPages = Math.round(totalCount / 10);

    if (getParameterValue()["page"] != "" && getParameterValue()["page"] != null) {
        currentPage = getParameterValue()["page"];
        if (totalPages == currentPage) {
            $("#btn_next").css("display", "none");
        } else if (currentPage == 1) {
            $("#btn_prev").css("display", "none");
        } else {
            $("#btn_prev").css("display", "inline");
        }
    } else {
        $("#btn_prev").css("display", "none");
        currentPage = 1;
    }
    
    $("#btn_prev").click(function () {
        if (currentPage != "" && currentPage != null) {
            var page = parseInt(currentPage);
            var pageRedirect = parseInt(page - 1);
            navigateNextPrev(pageRedirect);
        }
    });

    $("#btn_next").click(function () {
        if (currentPage != "" && currentPage != null) {
            var page = parseInt(currentPage);
            var pageRedirect = parseInt(page + 1);
            navigateNextPrev(pageRedirect);
        }
    });

    $('#edit-manager-submit').on('click', function (event) {
        validateInputs();
    });
});

function getParameterValue() {
    var params = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        params.push(hash[0]);
        params[hash[0]] = hash[1];
    }
    return params;
}

function navigateNextPrev(page) {
    var url = new URL(window.location.href);
    var search_params = url.searchParams;
    search_params.set('page', page);
    url.search = search_params.toString();
    var new_url = url.toString();
    window.location = new_url;
}

function isEmailValid(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

function validateInputs() {
    var mgr_name = $("#manager-name").val();
    var mgr_rank = $("#manager-rank").val();
    var mgr_email = $("#manager-email").val();
    var mgr_phone_no = $("#manager-contact").val();

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
    } else {
        submitForm();
        return true;
    }
}

function submitForm() {
    var mgr_id = $("#mgr_id").val();
    var mgr_name = $("#manager-name").val();
    var mgr_rank = $("#manager-rank").val();
    var mgr_email = $("#manager-email").val();
    var mgr_phone_no = $("#manager-contact").val();
    var mgr_use_flag = $("#post-status").val();
    var comp_id = $("#companyID").val();

    var editmanagercontent = {
        mgr_id: mgr_id,
        mgr_name: mgr_name,
        mgr_rank: mgr_rank,
        mgr_email: mgr_email,
        mgr_phone_no: mgr_phone_no,
        mgr_use_flag: mgr_use_flag,
        comp_id: comp_id
    };

    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/updateProductManager',
        data: JSON.stringify(editmanagercontent),
        dataType: "json",
        success: function (response) {
            if (response.code == "200") {
                alert("Manager's Data Have Been Updated.");
            } else {
                alert("An error has occurred.");
            }
        }
    });
}