var downloadTimer;

$(document).ready(function () {
    // start generic search page
    $('#genericOrderNoSubmit').on("click", function () {
        var i_order_no = $("#genericOrderNoText").val();
        var i_order_process = "order_generic";
        genericOrderSearch(i_order_no, i_order_process);
    }).hide();

    $('#genericOrderNoText').keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $('#genericOrderNoSubmit').click();
        }
    });

    $("html").click(function () {
        $("#genericOrderNoText").focus();
    });
    // end generic search page

    $("#operationHoldOrderBtn").on("click", function () {
        var tracking_no = $("#operationHoldOrderTxt").val();
        operationHoldOrder(tracking_no);
    });

    $("#operationUnholdOrderBtn").on("click", function () {
        var tracking_no = $("#operationUnholdOrderTxt").val();
        operationUnholdOrder(tracking_no);
    });

    $("#operationHoldOrderTxt").keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $("#operationHoldOrderBtn").click();
        }
    });

    $('#operationUnholdOrderTxt').keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $('#operationUnholdOrderBtn').click();
        }
    });
});

function getOrderCheckedSummaryVO(order_no, status, message) {
    $("div[id='pageHeader']").hide();
    $("div[id='div-order-check-table']").hide();

    var summaryHeader = ' <h1 class="flushed-center">상품 검수: 검색 결과 [SCAN RESULT]</h1>';
    $('div#summaryPageHeader').html(summaryHeader);

    $.ajax({
        url: '/operation/commonSummary?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            const jsonObject = response.object;
            var summaryObject = '';
            var tracking_no = jsonObject.tracking_no === null ? "N/A" : jsonObject.tracking_no;
            var processed_time = jsonObject.processed_time === null ? "N/A" : jsonObject.processed_time;
            var process_status = jsonObject.process_status === null ? "N/A" : jsonObject.process_status;
            var processed_user = jsonObject.processed_user === null ? "N/A" : jsonObject.processed_user;
            var is_unshippable = jsonObject.unshippable;
            var page_description = '';

            var orderDate = new Date(jsonObject.order_date);
            var order_date = orderDate.getFullYear() + '-' + ('0' + (orderDate.getMonth() + 1)).slice(-2) + '-' + ('0' + orderDate.getDate()).slice(-2) +
                ' ' + orderDate.getHours() + ':' + ('0' + (orderDate.getMinutes())).slice(-2) + ':' + orderDate.getSeconds();
            
            if (jsonObject.processed_time != null) {
                var processedTime = new Date(jsonObject.processed_time);
                processed_time = processedTime.getFullYear() + '-' + ('0' + (processedTime.getMonth() + 1)).slice(-2) + '-' + ('0' + processedTime.getDate()).slice(-2) +
                    ' ' + processedTime.getHours() + ':' + ('0' + (processedTime.getMinutes())).slice(-2) + ':' + processedTime.getSeconds();
            }

            var order_status = '';
            var order_progress = '<tr>\
                                    <td class="summary-progress" colspan="12">\
                                        <div class="progress-status">\
                                            <ol class="progress" data-steps="7">\
                                                <li class="주문서발급"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/packing_slip_dark.png"><br>주문서 발급</span><span class="step"></span></li>\
                                                <li class="피킹"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/picking_dark.png"><br>피킹</span><span class="step"></span></li>\
                                                <li class="미출고 unshippable"><span class="step"></span><span class="name"><img class="status-icon"\
                                                            src="/images/icons/unshippable_black.png"><br>미출고</span></li>\
                                                <li class="검수"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/checking_dark.png"><br>검수</span><span class="step"></span></li>\
                                                <li class="패킹"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/packing_dark.png"><br>패킹</span><span class="step"></span></li>\
                                                <li class="출고"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/confirmation_dark.png"><br>출고</span><span class="step"></span></li>\
                                                <li class="배송중"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/shipping_dark.png"><br>배송중</span><span class="step"></span></li>\
                                                <li class="배송완료"><span class="name"><img class="status-icon"\
                                                            src="/images/icons/delivered_dark.png"><br>배송완료</span><span class="step"></span></li>\
                                            </ol>\
                                        </div>\
                                    </td>\
                                </tr>';

            switch (jsonObject.order_status) {
                case "wc-shipped":
                    order_status = "배송중";
                    page_description = "배송중 [SHIPMENT IN-TRANSIT]";
                    break;
                case "wc-completed":
                    order_status = "배송 완료";
                    page_description = "배송 완료 [SHIPMENT DELIVERED]";
                    break;
                case "wc-processing":
                    order_status = "주문서 발급";
                    page_description = "주문서 발급 [ORDER PACKING SLIP VALIDATION]";
                    break;
                case "wc-preparing":
                    if (status == "검수") {
                        order_status = "검수";
                        page_description = "검수 [ORDER INSPECTION]";
                    } else if (status == "패킹") {
                        order_status = "패킹";
                        page_description = "패킹";
                    } else if (status == "출고") {
                        order_status = "출고";
                        page_description = "출고";
                    } else {
                        order_status = "주문서 발급";
                        page_description = "주문서 발급";
                    }
                    break;
                case "wc-domestic":
                    order_status = "고국 배송";
                    page_description = "고국 배송 [KOKOOK SHIPMENT]";
                    order_progress = '<tr>\
                                        <td class="summary-progress" colspan="12">\
                                            <div class="summary-short-desc" style="color: red;"> \
                                                <p>고국 배송은 물류 센터 내에서 처리되면 안되는 주문 건 입니다.</p>\
                                                <p>CX 팀에게 문의하세요.</p>\
                                            </div>\
                                        </td>\
                                    </tr>';
                    break;
                case "wc-cancelled":
                    order_status = "주문 실패";
                    page_description = "주문 실패 [ORDER FAILURE]";
                    order_progress = '<tr>\
                                        <td class="summary-progress" colspan="12">\
                                            <div class="progress-status" style="width: 80%; margin: 0 auto; height: 200px;">\
                                                <ol class="progress" data-steps="2">\
                                                    <li class="상품 주문 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/order_black.png"><br>상품 주문</span><span class="step"></span></li>\
                                                    <li class="주문 실패 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/cancel_black.png"><br>주문 실패</span><span class="step"></span></li>\
                                                </ol>\
                                            </div>\
                                        </td>\
                                    </tr>';
                    break;
                case "wc-refunded":
                    order_status = "환불 완료";
                    page_description = "환불 완료 [ORDER REFUNDED]";
                    order_progress = '<tr>\
                                        <td class="summary-progress" colspan="12">\
                                            <div class="progress-status" style="width: 80%; margin: 0 auto; height: 200px;">\
                                                <ol class="progress" data-steps="3">\
                                                    <li class="주문 결제 완료 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/order_black.png"><br>주문 결제 완료</span><span class="step"></span></li>\
                                                    <li class="응대 완료 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/cs_black.png"><br>응대 완료</span><span class="step"></span></li>\
                                                    <li class="환불 완료 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/refund_black.png"><br>환불 완료</span><span class="step last"></span></li>\
                                                </ol>\
                                            </div>\
                                        </td>\
                                    </tr>';
                    break;
                case "wc-unshippable":
                    order_status = "미출고";
                    page_description = "미출고 [UNSHIPPABLE ORDER]";
                    order_progress = '<tr>\
                                        <td class="summary-progress" colspan="12">\
                                            <div class="progress-status">\
                                                <ol class="progress" data-steps="7">\
                                                    <li class="주문서발급 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/packing_slip_dark.png"><br>주문서 발급</span><span class="step"></span></li>\
                                                    <li class="피킹 active"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/picking_dark.png"><br>피킹</span><span class="step last"></span></li>\
                                                    <li class="미출고 active unshippable"><span class="step"></span><span class="name"><img class="status-icon"\
                                                                src="/images/icons/unshippable_black.png"><br>미출고</span></li>\
                                                    <li class="검수"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/checking_dark.png"><br>검수</span><span class="step"></span></li>\
                                                    <li class="패킹"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/packing_dark.png"><br>패킹</span><span class="step"></span></li>\
                                                    <li class="출고"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/confirmation_dark.png"><br>출고</span><span class="step"></span></li>\
                                                    <li class="배송중"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/shipping_dark.png"><br>배송중</span><span class="step"></span></li>\
                                                    <li class="배송완료"><span class="name"><img class="status-icon"\
                                                                src="/images/icons/delivered_dark.png"><br>배송완료</span><span class="step"></span></li>\
                                                </ol>\
                                            </div>\
                                        </td>\
                </tr>';
                    break;
                case "wc-on-hold":
                    order_status = "임시 보관";
                    page_description = "임시 보관 [ON HOLD]";
                    break;
                default:
                    order_status = (jsonObject.order_status == null || jsonObject.order_status == '') ? "N/A" : jsonObject.order_status;
                    break;
            }

            var summaryList = '';

            $.each(jsonObject.transactions, function(i, object) {
                var transactionDate = new Date(object.trans_date);
                var transaction_date = transactionDate.getFullYear() + '-' + ('0' + (transactionDate.getMonth() + 1)).slice(-2) + '-' +
                    ('0' + transactionDate.getDate()).slice(-2) + ' ' + transactionDate.getHours() + ':' + ('0' + (transactionDate.getMinutes())).slice(-2) +
                    ':' + transactionDate.getSeconds();

                summaryList += '<tr class="row-white" id="summaryList"><td class="summary-processed-time"> ' + transaction_date + ' </td>' +
                    '<td class="summary-task-classification"> ' + object.order_process + ' </td>' +
                    '<td class="summary-processed-by"> ' + object.pic + ' </td></tr>';
            });

            summaryObject += '<tr><td class="summary-short-desc" colspan="3">' + page_description + ' </td></tr>' +
                '<tr><td class="summary-order-no">주문 번호: #' + jsonObject.order_no + ' </td>' +
                '<td class="summary-order-status" data-status="' + order_status.replace(/\s+/, "") + '"> 주문 상태: ' + order_status + ' </td>' +
                '<td class="summary-order-date"> 주문 일자: ' + order_date + '</td></tr>' +
                '<tr><td colspan="12" id="warningMsg" class="warning-msg-summary">' + message + '</td></tr>' +
                order_progress +
                '<tr class="row-white"><td class="summary-dhl-icon"> <img class="dhl-icon" src="/images/icons/dhl.png"><br/> <p>DHL Express</p> <p>Worldwide</p> </td>' +
                '<td class="summary-order-inv"> <p>송장 번호: ' + tracking_no + '</p> <p>출력 일자: ' + processed_time + '</p> </td>' +
                '<td class="summary-customer-info"> <p>주문자 정보</p> <p>주문자: ' + jsonObject.customer_name + '</p> <p>이메일: ' + jsonObject.customer_email + '</p> <p>지역: ' + jsonObject.customer_state + '</p> </td></tr>'

            +
            '<tr><td class="summary-processed-time-header"> 시간 </td>' +
            '<td class="summary-task-class-header"> 작업 구분 </td>' +
            '<td class="summary-processed-by-header"> 처리자 </td></tr>' +
            summaryList
                +
                '<tr><td class="redirect-countdown"> <div id="countdown"></div> </td>' +
                '<td class="abort-redirect"> <input type="button" onclick="abortRedirect();" class="button send-notification primary" value="중단" id="abortRedirect"> </td>' +
                '<td class="redirect-scan-page"> <button onclick="redirectToOperation()" class="send-notification primary " id="redirectToScanPage"> 스캔 화면으로 </button></td></tr>';

            $('tbody#orderSummary').html(summaryObject);

            if (message != null && message != "") {
                $("#warningMsg").show();
            } else {
                $("#warningMsg").hide();
            }

            if (jsonObject.order_status != 'wc-unshippable' && is_unshippable == '1') {
                $(".unshippable").addClass("active");
                $(".unshippable .step").addClass("done");
                $('.unshippable').prev().children('.step').addClass('unship-l');
                $('.unshippable').next().children('.step').addClass('unship-r');
            }

            redirectUrlWithTimeout();
            showCountdown();
            updateProgress();
        },
        error: function() {
            console.log("error");
        }
    });
}

function updateProgress() {
    var statusProgress = $('.summary-order-status').data('status');
    $('.' + statusProgress).addClass('active');
    $('.active').prevAll('li').not('.unshippable').addClass('active');
    $('.active .step').last().addClass('last');
}

function genericValidateOrderNo(order_no, order_process) {
    var data = $.ajax({
        url: '/operation/genericValidateOrderNo?order_no=' + order_no + '&order_process=' + order_process,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            console.log("success");
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });

    return data;
}

function showCountdown() {
    var timeleft = 10;
    downloadTimer = setInterval(function() {
        if (timeleft <= 0) {
            clearInterval(downloadTimer);
            document.getElementById("countdown").innerHTML = "초 후에 스캔 화면으로 돌아갑니다...";
        } else {
            document.getElementById("countdown").innerHTML = timeleft + " 초 후에 스캔 화면으로 돌아갑니다...";
        }
        timeleft -= 1;
    }, 1000);
}

function abortRedirect() {
    clearTimeout(redirect_);
    clearTimeout(downloadTimer);
    $("#countdown").html("");
}

function genericOrderSearch(order_no, order_process) {
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

            if (code == '200') {
                $("div[id='div-main']").hide();
                $("div[id='pageDescription_1']").hide();
                getOrderCheckedSummaryVO(order_no, status, message);
            } else if (code == '404') {
                alert("주문 번호가 올바르지 않거나, 존재하지 않습니다");
                $("#genericOrderNoText").val("")
            } else {
                alert("An error has occurred");
                $("#genericOrderNoText").val("")
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function redirectUrlWithTimeout() {
    redirect_ = setTimeout(function () {
        redirectToOperation();
    }, 10500);
}

function redirectToOperation() {
    var href = window.location.href;
    var redirect = "";
    if (href.indexOf("-inspection") > -1) {
        redirect = "/operation/order-inspection-v2";
    } else if (href.indexOf("-confirmation") > -1) {
        redirect = "/operation/order-confirmation";
    } else if (href.indexOf("-packing") > -1) {
        redirect = "/operation/order-packing";
    } else {
        redirect = "/operation/generic-search-order";
    }

    window.location = redirect;
}

function operationHoldOrder(tracking_no) {
    $.ajax({
        url: '/operation/operationHoldOrder?tracking_no=' + tracking_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            var code = response.code;
            var message = response.message;

            if (code == '200') {
                $("#promptBox .modal-content .alert").removeClass("alert_warning").addClass("alert_success");
                $("#promptBox .modal-content .alert").html("<div class='alert--icon'><i class='fas fa-check-circle'></i></div><div class='alert--content'>" + message + "</div>");
                $("#promptBox").fadeIn(500, function () {
                    $(this).fadeOut(4000);
                });
                $("#operationHoldOrderTxt").val("");
            } else {
                $("#promptBox .modal-content .alert").removeClass("alert_success").addClass("alert_warning");
                $("#promptBox .modal-content .alert").html("<div class='alert--icon'><i class='fas fa-exclamation'></i></div><div class='alert--content'>" + message + "</div>");
                $("#promptBox").fadeIn(500, function () {
                    $(this).fadeOut(4000);
                });
                $("#operationHoldOrderTxt").val("");
            }
        },
        error: function () {
            $("#promptBox .modal-content .alert").removeClass("alert_success").addClass("alert_warning");
            $("#promptBox .modal-content .alert").html("<div class='alert--icon'><i class='fas fa-exclamation'></i></div><div class='alert--content'>An error has occurred, please contact Dev.</div>");
            $("#promptBox").fadeIn(500, function () {
                $(this).fadeOut(4000);
            });
            $("#operationHoldOrderTxt").val("");
        }
    });
}

function operationUnholdOrder(tracking_no) {
    $.ajax({
        url: '/operation/operationUnholdOrder?tracking_no=' + tracking_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            var code = response.code;
            var message = response.message;

            if (code == '200') {
                $("#promptBox .modal-content .alert").removeClass("alert_warning").addClass("alert_success");
                $("#promptBox .modal-content .alert").html("<div class='alert--icon'><i class='fas fa-check-circle'></i></div><div class='alert--content'>" + message + "</div>");
                $("#promptBox").fadeIn(500, function () {
                    $(this).fadeOut(4000);
                });
                $("#operationUnholdOrderTxt").val("");
            } else {
                $("#promptBox .modal-content .alert").removeClass("alert_success").addClass("alert_warning");
                $("#promptBox .modal-content .alert").html("<div class='alert--icon'><i class='fas fa-exclamation'></i></div><div class='alert--content'>" + message + "</div>");
                $("#promptBox").fadeIn(500, function () {
                    $(this).fadeOut(4000);
                });
                $("#operationUnholdOrderTxt").val("");
            }
        },
        error: function () {
            $("#promptBox .modal-content .alert").removeClass("alert_success").addClass("alert_warning");
            $("#promptBox .modal-content .alert").html("<div class='alert--icon'><i class='fas fa-exclamation'></i></div><div class='alert--content'>An error has occurred, please contact Dev.</div>");
            $("#promptBox").fadeIn(500, function () {
                $(this).fadeOut(4000);
            });
            $("#operationHoldOrderTxt").val("");
        }
    });
}