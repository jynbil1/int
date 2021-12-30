var redirect_;
var summary_details;
var isValidUser;
var downloadTimer;
var order_process = 'order_confirmation';
var is_confirm = false;
var userIP;
var bedalPort = ':3654';
var length, width, weight, height;

$(document).ready(function () {

    getClientPublicIPAddress();

    $('#orderNoSubmit').on("click", function () {
        var i_tracking_no = $("#fetchLandingPage").val();
        var skip_bedal = $("#chkSkipBedal").is(":checked");
        getOrderNoByTrackingNo(i_tracking_no, skip_bedal);
    }).hide();

    $('#fetchLandingPage').keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $('#orderNoSubmit').click();
        }
    });

    $("html").click(function () {
        $("#fetchLandingPage").focus();
    });
});

function getClientPublicIPAddress() {
    $.getJSON('https://ipinfo.io/json', function (data) {
        userIP = data.ip;
    });
}

function toggleCheckOrderListAndSummary(order_no, order_process, skip_bedal) {
    $.ajax({
        url: '/operation/genericValidateOrderNo?order_no=' + order_no + '&order_process=' + order_process,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log(response);
            var code = response.code;
            var status = response.status;
            var message = response.message;

            if (code == '400' || code == '405') {
                getOrderCheckedSummaryVO(order_no, status, message);
                $("div[id=divConfirmationTable]").show();
                $("div[id='divConfirmationPage']").hide();
                $("div[id='divOrderList']").hide();
                $("div[id='pageDescription']").hide();
            } else if (code == '200') {
                if (isValidUser === true) {
                    checkBedalMeasurement(order_no, skip_bedal);
                } else {
                    if (confirm(isValidUser + ' is currently working on this order, which means you cannot make changes, unless you take over.')) {
                        checkBedalMeasurement(order_no, skip_bedal);
                    } else {
                        redirectToOperation();
                        console.log('No changes has been made.');
                    }
                }
            } else {
                alert("An error has occurred");
                $("#fetchLandingPage").val("")
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function abortRedirect() {
    clearTimeout(redirect_);
    clearTimeout(downloadTimer);
    $("#countdown").html("");
}

function checkBedalMeasurement(order_no, skip_bedal) {
    $(".loading").css("display", "table");
    if (skip_bedal) {
        var payload = {
            "order_no": order_no,
            "wh_cnf_width": "30",
            "wh_cnf_length": "30",
            "wh_cnf_height": "30",
            "wh_cnf_weight": "30",
            "unit": "cm"
        }
        insertBedalMeasurement(payload);
        width = "30";
        length = "30";
        weight = "30";
        height = "30";

        $("div[id='divConfirmationPage']").show();
        $("div[id='divOrderList']").hide();
    } else {
        getBedalMeasurementData(order_no);
    }
}

function callValidateCheckingUser(order_no, order_process, bedal_scan) {
    $.ajax({
        url: '/operation/isCheckingUserValid?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log(JSON.stringify(response));
            if (response.object === true) {
                isValidUser = response.object;
            } else {
                isValidUser = response.message;
            }
            toggleCheckOrderListAndSummary(order_no, order_process, bedal_scan);
        },
        error: function () {
            console.log("error");
        }
    });
}

function getBedalMeasurementData(order_no) {
    $.ajax({
        type: 'GET',
        url: 'http://' + userIP + bedalPort,
        dataType: 'jsonp', // Using Cross-Origin Resource Sharing
        jsonpCallback: 'callback',
        success: function (data) {
            $(".loading").css("display", "none");
            var payload = {
                "order_no": order_no,
                "wh_cnf_width": data.width.replace(/[^\d.-]/g, ""),
                "wh_cnf_length": data.length.replace(/[^\d.-]/g, ""),
                "wh_cnf_height": data.height.replace(/[^\d.-]/g, ""),
                "wh_cnf_weight": data.weight.replace(/[^\d.-]/g, ""),
                "unit": "cm"
            }
            console.log(payload);
            if (width == data.width && length == data.length && height == data.height && weight == data.weight) { 
                alert("Weight and dimensions seem to be the same with the previous parcel, please recheck.");
            } else if (data.weight == null || data.weight == "" || data.weight.replace(/[^\d.-]/g, "") == 0) {
                alert("Weight is invalid, please recheck.");
            } else if (data.width == null || data.width == "" || data.width.replace(/[^\d.-]/g, "") == 0) {
                alert("Width is invalid, please recheck.");
            } else if (data.height == null || data.height == "" || data.height.replace(/[^\d.-]/g, "") == 0) {
                alert("Height is invalid, please recheck.");
            } else if (data.length == null || data.length == "" || data.length.replace(/[^\d.-]/g, "") == 0) {
                alert("Length is invalid, please recheck.");
            } else {
                insertBedalMeasurement(payload);
                width = data.width;
                length = data.length;
                weight = data.weight;
                height = data.height;
            }

            $("div[id='divConfirmationPage']").show();
            $("div[id='divOrderList']").hide();
        },
        error: function (xhr, status, err) {
            $("#errorPrompt").fadeIn(500, function () {
                $(this).fadeOut(4000);
            });
            $(".loading").css("display", "none");
            $("div[id='divConfirmationPage']").show();
            $("div[id='divOrderList']").hide();
        }
    });
}

function insertBedalMeasurement(payload) {
    $.ajax({
        url: '/operation/insertConfirmationDetails',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payload),
        success: function (response) {
            $("#promptBox").fadeIn(500, function () {
                $(this).fadeOut(4000);
            });
            $("#fetchLandingPage").val('');
            $('#chkSkipBedal').prop('checked', true);
            $(".loading").css("display", "none");
        },
        error: function () {
            console.log("error");
            $(".loading").css("display", "none");
        }
    });
}

function getOrderNoByTrackingNo(tracking_no, bedal_scan) {
    $.ajax({
        url: '/operation/getOrderMenuByTrackingNo?tracking_no=' + tracking_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                var jsonObject = response.object;
                console.log(jsonObject);
                var count = Object.keys(jsonObject).length;
                if (count > 1) {
                    $("#divOrderList").show();
                    $("#divConfirmationPage").hide();
                    var tr = '';
                    $.each(jsonObject, function (i, object) {
                        var orderDate = '';
                        if (object.order_date != null && object.order_date != '') {
                            var date = new Date(object.order_date);
                            orderDate = [
                                date.getFullYear(),
                                ('0' + (date.getMonth() + 1)).slice(-2),
                                ('0' + date.getDate()).slice(-2)
                            ].join('-');
                        }

                        tr += '<tr data-order-no="' + object.order_no + '">' +
                            '<td class="order-date">' + orderDate + '</td>' +
                            '<td class="order-no">' + object.order_no + '</td>' +
                            '<td class="tracking-no">' + object.tracking_no + '</td>' +
                            '<td class="sku">' + object.sku + '</td>' +
                            '<td><input type="button" class="primary button proceed-btn" value="Proceed"></td>'
                        '</tr>';
                    });
                    $('tbody#orderList').html(tr);
                    $(".proceed-btn").on("click", function () {
                        var i_order_no = $(this).parent().siblings(".order-no").text();
                        callValidateCheckingUser(i_order_no, order_process, bedal_scan);
                    });
                } else {
                    var i_order_no = jsonObject[0].order_no;
                    callValidateCheckingUser(i_order_no, order_process, bedal_scan);
                }
            } else {
                $('.warning-sound').get(0).play();
                alert("주문 번호가 올바르지 않거나, 존재하지 않습니다");
                $("#fetchLandingPage").val("");
            }
        },
        async: true,
        error: function () {
            alert("An error has occurred.");
        }
    });
}