var sortBy;
var orderBy;
var currentPage;
var itemRange;
var searchKeyword;
var isUrgent = 0;
var orderDate;
var seeAll = 1;
var isToday = 0;

var datePlaceHolder;
var clickedOrderSeq;

var orderNoteOpen = {};
var productsOpen = {};

$(document).ready(function() {

    currentPage = $("#listPage").val();
    itemRange = $("#itemRange").val();
    searchKeyword = $(".search > input").val();
    sortBy = $("#sortBy").val();
    orderBy = $("#orderBy").val();

    // getOrderList();
    getOrdersData();

    // 긴급 요청건만 보기
    $("body").on("click", "#show-urgent-list", function() {
        isUrgent = $('#show-urgent-list').is(":checked") ? 1 : 0;
        // getOrderList();
        getOrdersData();
    });

    // 금일 일자 발주건만 보기
    $("body").on("click", "#see-today", function() {
        seeAll = $('#see-today').is(":checked") ? 0 : 1;
        isToday = $('#see-today').is(":checked") ? 1 : 0;
        if (seeAll == 1) {
            orderDate = '';
        } else {
            orderDate = new Date().format('yyyy-MM-dd');
        }
        // getOrderList();
        getOrdersData();
    });

    // 날짜 기준 검색
    $(".search").change(function() {
        orderDate = $('#order-date-filter').val();
        if (orderDate.length > 6) {
            seeAll = 0;
        } else {
            seeAll = 1;
        }
        // getOrderList();
        getOrdersData();
    });

    // 컬럼별 정렬
    $("#orderDate, #orderSeq, #orderSubtotal, #orderCompany, #shippingFee,\
        #orderTotal ,#orderer, #evidType, #isPaid, #payDeadline,\
        #payIssue ,#payer,#isUrgent, #isBillValidated, #remarks, #enterStatus,\
        #arrivalUser, #trackShipment").on("click", function() {
        orderBy = $(this).attr("data-order");
        sortBy = $(this).attr("data-sort");

        if (orderBy == 1) {
            $(this).attr("data-order", 0);
            searchKeyword = $(".search > input").val();
            currentPage = 1;
            $("#listPage").val(currentPage);
            getOrdersData();
            $("#orderBy").val(orderBy);
            $("#sortBy").val(sortBy);
        } else {
            $(this).attr("data-order", 1);
            searchKeyword = $(".search > input").val();
            currentPage = 1;
            $("#listPage").val(currentPage);
            getOrdersData();
            $("#orderBy").val(orderBy);
            $("#sortBy").val(sortBy);
        }
    });

    // 검색 버튼을 누르면 검색 수행.
    $(".search > button").on("click", function(event) {
        event.preventDefault();
        searchKeyword = $(".search > input").val();
        currentPage = 1;
        $("#listPage").val(currentPage);
        $("#searchKeyword").val(searchKeyword);
        getOrdersData();
        // getOrderList();

    });

    // 검색어 입력 후 엔터 시 검색
    $(".search > input").keypress(function(e) {
        var key = e.which;
        if (key == 13) {
            currentPage = 1;
            $("#listPage").val(currentPage);
            searchKeyword = $(this).val().trim();
            $("#searchKeyword").val(searchKeyword);
            getOrdersData();
            // getOrderList();
        }
    });

    // 한 페이지에서 볼 수 있는 데이터 수 적용
    $('#item_range').change(function() {
        currentPage = 1;
        $("#listPage").val(currentPage);
        itemRange = $(this).val();
        orderBy = $("#orderBy").val();
        $("#itemRange").val(itemRange);
        getOrdersData();
        // getOrderList();

    });

    // 해당 값의 로우 클릭하면, 추가 확장 데이터(발주 상품 리스트) 나오게 하는 기능
    $("body").on("click",
        "#order-history-list tr td.order-date, #order-history-list tr td.order-seq, #order-history-list tr td.comp-name, #order-history-list tr td.order-subtotal, #order-history-list tr td.shipping-fee,\
        #order-history-list tr td.order-total, #order-history-list tr td.hanpoom-pic, #order-history-list tr td.payment-deadline, #order-history-list tr td.paid-issue, #order-history-list tr td.payer,\
        #order-history-list tr td.paid-remarks, #order-history-list tr td.enter-status, #order-history-list tr td.arrival-user",
        function() {
            var orderSeq = $(this).parent().attr("data-order-seq");
            if (productsOpen[orderSeq] === 0) {
                getOrderProducts(orderSeq);
                productsOpen[orderSeq] = 1;
                $(this).closest('tr').next('tr').show();
            } else {
                $(this).closest('tr').next('tr').hide();
                productsOpen[orderSeq] = 0;
            }
        });

    // 해당 값의 로우 클릭하면, 각 발주 상품의 order note 가 나옴.
    $("body").on("click",
        "#order-history-list tr td tbody tr td.order-detail-seq, #order-history-list tr td tbody tr td.product-id, #order-history-list tr td tbody tr td.product-name, #order-history-list tr td tbody tr td.ship-status",
        function() {
            var orderDetailSeq = $(this).parent().attr("data-order-detail-seq");
            if (orderNoteOpen[orderDetailSeq] === 0) {
                orderNoteOpen[orderDetailSeq] = 1;
                $(this).closest('tr').next('tr').show();
            } else {
                $(this).closest('tr').next('tr').hide();
                orderNoteOpen[orderDetailSeq] = 0;
            }
        });

    // // 생성된 발주 메모를 누르면 다시 사라지는 기능.
    // $("body").on("click", ".order-note-title", function() {
    //     orderOpen = 0;
    //     $(this).parent().hide();
    // });



    // 발주 제거 기능.
    $("body").on("click", ".delete-order-btn", function() {
        var orderSeq = $(this).parent().parent().attr("data-order-seq");
        // var productId = $(this).parent().parent().children(".product-id").text();
        console.log('delete', orderSeq);
        deleteOrder(orderSeq);
    });

    // 발주 상품 제거 기능.
    $("body").on("click", ".delete-order-seq-btn", function() {
        var orderDetailSeq = $(this).parent().parent().attr("data-order-detail-seq");
        // var productId = $(this).parent().parent().children(".product-id").text();
        console.log('delete', orderDetailSeq);
        deleteOrderProduct(orderDetailSeq);
    });


    // 발주 상품 메모 갱신 기능 (버튼 클릭시)
    $("body").on("click", ".update-order-note-btn", function() {
        var orderDetailSeq = $(this).closest('tr').prev('tr').attr("data-order-detail-seq");
        var productId = $(this).closest('tr').prev('tr').children(".product-id").text();
        var orderNote = $('[data-order-detail-seq="' + orderDetailSeq + '"] + .order-note .order-note-input').val();
        console.log('update note', orderDetailSeq, productId, orderNote);
        updateOrderNote(orderDetailSeq, productId, orderNote);
    });

    // 발주 상품 메모 갱신 기능 (입력 항목에서 엔터 시)
    $("body").on("keypress", ".order-note-input", function(key) {
        var orderDetailSeq = $(this).closest('tr').prev('tr').attr("data-order-detail-seq");
        var productId = $(this).closest('tr').prev('tr').children(".product-id").text();
        var orderNote = $('[data-order-detail-seq="' + orderDetailSeq + '"] + .order-note .order-note-input').val();
        if (key.keyCode == 13) {
            console.log('update note enter', orderDetailSeq, productId, orderNote);
            updateOrderNote(orderDetailSeq, productId, orderNote);
        }
    });

    // 해당 항목들에 대해 값이 바뀔경우 유동적인 값 갱신 작업이 필요.
    $("body").on("change", ".order-qty-input, .unit-price-input, .shipping-fee-input", function() {
        var orderSeq = $(this).parent().parent().attr("data-order-detail-seq");

        var unitPrice = Number($('[data-order-detail-seq="' + orderSeq + '"] .unit-price-input').val().replace(/,/g, ''));
        var orderProductQty = Number($('[data-order-detail-seq= "' + orderSeq + '"] .order-qty-input').val().replace(/,/g, ''));
        var shippingFee = Number($('[data-order-detail-seq="' + orderSeq + '"] .shipping-fee-input').val().replace(/,/g, ''));
        console.log(unitPrice);
        var newSum = addCommas(((orderProductQty * unitPrice) + shippingFee).toString());
        $('[data-order-detail-seq="' + orderSeq + '"] .line-total').html(newSum);

        var newUnitPrice = $('[data-order-detail-seq="' + orderSeq + '"] .unit-price-input');
        var newProductQty = $('[data-order-detail-seq= "' + orderSeq + '"] .order-qty-input');
        var newShippingFee = $('[data-order-detail-seq= "' + orderSeq + '"] .shipping-fee-input');
        console.log(newUnitPrice.val());

        newProductQty.val(addCommas(newProductQty.val().replace(/,/g, '')));
        newUnitPrice.val(addCommas(newUnitPrice.val().replace(/,/g, '')));
        newShippingFee.val(addCommas(newShippingFee.val().replace(/,/g, '')));

    });

    // 해당 값이 0 이면 클릭했을 때, 빈 값으로 만들어 줄 것.
    $("body").on("focusin", ".order-qty-input, .unit-price-input, .shipping-fee-input", function() {
        var orderDetailSeq = $(this).parent().parent().attr("data-order-detail-seq");
        var orderQtyInput = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .unit-price-input');
        var unitPriceInput = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .order-qty-input');
        var shippingFeeInput = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .shipping-fee-input');

        if (orderQtyInput.val() == 0) {
            orderQtyInput.val('');
        }
        if (unitPriceInput.val() == 0) {
            unitPriceInput.val('');
        }
        if (shippingFeeInput.val() == 0) {
            shippingFeeInput.val('');
        }
    });

    // 입력하지 않고 넘어가면 자동으로 0을 채워줄 것..
    $("body").on("focusout", ".order-qty-input, .unit-price-input, .shipping-fee-input", function() {
        var orderDetailSeq = $(this).parent().parent().attr("data-order-detail-seq");
        var orderQtyInput = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .unit-price-input');
        var unitPriceInput = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .order-qty-input');
        var shippingFeeInput = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .shipping-fee-input');

        if (orderQtyInput.val() == '') {
            orderQtyInput.val('0');
        }
        if (unitPriceInput.val() == '') {
            unitPriceInput.val('0');
        }
        if (shippingFeeInput.val() == '') {
            shippingFeeInput.val('0');
        }
    });

    // 업데이트 버튼을 누르면 발주 상품 데이터 한개의 값을 수정.
    $("body").on("click", ".update-order-btn", function() {
        var orderDetailSeq = $(this).parent().parent().attr("data-order-detail-seq");
        // var productId = $(this).parent().parent().children(".product-id").text();
        var orderProductQty = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .order-qty-input').val();
        var unitPrice = $('[data-order-detail-seq="' + orderDetailSeq + '"] .unit-price-input').val();
        var shipping_fee = $('[data-order-detail-seq="' + orderDetailSeq + '"] .shipping-fee-input').val();
        uppdateOrderedProducts(orderDetailSeq, orderProductQty, unitPrice, shipping_fee);
    });

    // 배송비 칸에서 엔터나 탭키를 누르면 값 갱신 작업 수행.
    $("body").on("keypress", ".shipping-fee-input", function(key) {
        var orderDetailSeq = $(this).parent().parent().attr("data-order-detail-seq");
        // var productId = $(this).parent().parent().children(".product-id").text();
        var orderProductQty = $('[data-order-detail-seq= "' + orderDetailSeq + '"] .order-qty-input').val();
        var unitPrice = $('[data-order-detail-seq="' + orderDetailSeq + '"] .unit-price-input').val();
        var shipping_fee = $('[data-order-detail-seq="' + orderDetailSeq + '"] .shipping-fee-input').val();
        if (key.keyCode === 13 || key.keycode === 9) { // enter || tab
            if (shipping_fee == '') {
                shipping_fee = '0';
                $('[data-order-detail-seq="' + orderDetailSeq + '"] .shipping-fee-input').val(0);
            }
            uppdateOrderedProducts(orderDetailSeq, orderProductQty, unitPrice, shipping_fee);
        }
    });

    // 긴급 요청 버튼이 눌러지면,
    $("body").on("click", ".urgent-payment-chk-box", function() {
        var self = $(this).parent().parent();
        var is_checked = self.find("[name='urgent-cb']").is(":checked");
        var md_order_seq = $(this).val();

        if (is_checked) {
            markUrgentTransaction(1, md_order_seq);
        } else {
            markUrgentTransaction(0, md_order_seq);
        }

        $(this).parent().parent().addClass("urgent-row");

    });

    $("body").on("click", ".bill-validation-chk-box", function() {
        var self = $(this).parent().parent();
        var is_checked = self.find("[name='bill-validation-cb']").is(":checked");
        var md_order_seq = $(this).val();

        if (is_checked) {
            markBillValidation(1, md_order_seq);
            $(this).parent().parent().addClass("bill-validation-row");
        } else {
            markBillValidation(0, md_order_seq);
            $(this).parent().parent().removeClass("bill-validation-row");
        }
    });

});



function markUrgentTransaction(is_checked, md_order_seq) {
    var urgentVo = {
        "is_urgent_transaction": is_checked,
        "md_order_seq": md_order_seq
    };
    $.ajax({
        url: '/finance/updateUrgentTransaction',
        type: 'POST',
        data: JSON.stringify(urgentVo),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                $(".notification-msg").html('<i class="fas fa-check-circle"></i>' + md_order_seq + ' 의 긴급결제 요청이 정상 처리 되었습니다.').removeClass("warning").addClass("success");
            } else if (response.code == '403') {
                window.location.href = "/signIn";
            } else if (response.code == '500') {
                alert("MD Order Sequence is not found.");
            } else if (response.code == "400") {
                $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>' + md_order_seq + ' 의 긴급결제 요청에 실패했습니다.').removeClass("success").addClass("warning");
                console.log(response.message);
            }
        }
    });
};

function markBillValidation(is_checked, md_order_seq) {

    var urgentVo = {
        "is_bill_validated": is_checked,
        "order_seq": md_order_seq
    };
    $.ajax({
        url: '/mfc/shipment/confirmBillValidationByAdmin',
        type: 'POST',
        data: JSON.stringify(urgentVo),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                $(".notification-msg").html('<i class="fas fa-check-circle"></i>' + md_order_seq + ' 의 결제 여부 처리 작업을 완료했습니다.').removeClass("warning").addClass("success");
            } else if (response.code == '503') {
                window.location.href = "/signIn";
            } else if (response.code == '500') {
                alert("MD Order Sequence is not found.");
            } else if (response.code == "400") {
                $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>' + md_order_seq + ' 의 결제 여부 처리 작업에 실패했습니다.').removeClass("success").addClass("warning");
                console.log(response.message);
            }
            $(".notification-message").animate({ opacity: 1 }, 500, function() {
                $('.notification-message').css('visibility', 'visible');
                $(".notification-message").animate({ opacity: 0 }, 3000, function() {
                    $('.notification-message').css('visibility', 'hidden');
                });
            });
        }
    });
};

function reloadCurrentPage() {
    // getOrderList();
    getOrdersData();
    totalPage = Math.ceil(count / itemRange);
    createPagination(currentPage, totalPage);
}

function getOrderList() {
    $.ajax({
        url: '/mis/order/history/getOrderProductList?page=' + currentPage + '&item_range=' +
            itemRange + '&keyword=' + searchKeyword + '&sort=' + sortBy + '&order=' +
            orderBy + '&date=' + orderDate + '&is_urgent=' + isUrgent + '&see_all=' +
            seeAll + '&is_today=' + isToday,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            console.log(response);
            if (response.code == '200') {
                const jsonObject = response.object;
                var tr = '';
                $.each(jsonObject, function(i, object) {
                    productsOpen[object.order_seq] = 0;
                    var formattedOrderDate = object.order_date.substr(2, 3) + object.order_date.substr(5, 10);
                    var formattedOrderSeq = object.order_seq.substr(0, 6) + '-' + object.order_seq.substr(6, 10);

                    var formattedSubtotal = addCommas(object.subtotal.toString());
                    var formattedShippingFee = addCommas(object.total_shipping_fee.toString());
                    var formattedTotal = addCommas(object.total.toString());
                    // var formattedIsPaid = object.is_paid

                    var formattedPayDeadline = object.pay_deadline.substr(2, 3) + object.pay_deadline.substr(5, 10);
                    var formattedPaidIssue = object.paid_issue == null ? "" : object.paid_issue;
                    var formattedPayer = object.payer == null ? "" : object.payer;
                    var formattedRemarks = object.remarks == null ? "" : object.remarks;
                    // var formattedHanpoomPic = object.hanpoom_pic

                    var formattedEnterStatus = object.enter_status === null ? "" : object.enter_status;

                    var formattedArrivalUser = object.arrival_user == null ? "" : object.arrival_user;
                    var formattedIsUrgent = object.is_urgent_transaction == 0 ? "" : "checked";
                    var formattedBillValidated = object.is_bill_validated == 0 ? "" : "checked";

                    var isPaidStatusClass = object.is_paid == "입금 대기" ? "incomplete" : object.is_paid == "입금 중" ? "less" : object.is_paid == "입금 완료" ? "completed" : object.is_paid == "입금 초과" ? "exceeded" : "";
                    var evidTypeClass = object.evid_type == "세금 계산서" ? "tax-invoice" : object.evid_type == "면세 계산서" ? "bill" : object.evid_type == "예치금(면세)" ? "deposit-no-tax" : object.evid_type == "예치금(과세)" ? "deposit-with-tax" : object.evid_type == "법인카드" ? "corporate" : "unknown";

                    var formattedTrackShipment = object.has_shipment_validated === 1 ? '<i class="dd-icon shipping-truck-green"></i>' : '<i class="dd-icon shipping-truck-red"></i>'
                    tr += '<tr data-order-seq="' + object.order_seq + '">' +

                        '<td class="order-date">' + formattedOrderDate + '</td>' +
                        '<td class="order-seq">' + formattedOrderSeq + '</td>' +
                        '<td class="comp-name">' + object.company_name + '</td>' +

                        '<td class="order-subtotal" >' + formattedSubtotal + '</td>' +
                        '<td class="shipping-fee">' + formattedShippingFee + '</td>' +
                        '<td class="order-total">' + formattedTotal + '</td>' +
                        '<td class="hanpoom-pic">' + object.orderer + '</td>' +

                        '<td><span class="' + evidTypeClass + '">' + object.evid_type + '</span></td>' +
                        '<td><span class="' + isPaidStatusClass + '">' + object.is_paid + '</span></td>' +
                        '<td class="payment-deadline">' + formattedPayDeadline + '</td>' +
                        '<td class="paid-issue">' + formattedPaidIssue + '</td>' +
                        '<td class="payer">' + formattedPayer + '</td>' +
                        '<td class="urgent-payment"><input class="urgent-payment-chk-box" value="' + object.order_seq + '"\
                            type="checkbox" name="urgent-cb" ' + formattedIsUrgent + '></td>' +
                        '<td class="bill-validation"><input class="bill-validation-chk-box" value="' + object.order_seq + '"\
                            type="checkbox" name="bill-validation-cb" disabled ' + formattedBillValidated + '></td>' +
                        '<td class="paid-remarks">' + formattedRemarks + '</td>' +
                        '<td class="enter-status">' + formattedEnterStatus + '</td>' +
                        '<td class="arrival-user">' + formattedArrivalUser + '</td>' +
                        '<td data-id="' + object.order_seq + '">' +
                        '<div class="ship-tracking-btn">' + formattedTrackShipment + '</div>' +
                        '</td>' +
                        '<td class="delete-button"><input class="delete-order-btn button primary" type="button" value="X"></td>' +
                        '</tr>' +
                        '<tr class="hidden" data-order-seq-list="' + object.order_seq + '"><td colspan="19">' +
                        '<table> <thead></thead> <tbody></tbody> </table>'
                });

                $('tbody#order-history-list').html(tr);
                $(".ship-tracking-btn").on("click", function() {
                    orderSeq = $(this).parent().data('id');
                    console.log(orderSeq);
                    getshipmentTrackingBySeq(orderSeq);
                });

            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                var tr = '<tr class="result-message">\
                            <td colspan="19">\
                                검색 결과가 없습니다.\
                            </td>\
                        </tr>';
                $('tbody#order-history-list').html(tr);
                createPagination(0, 0);
                $("#totalOrders").html('0');
            } else {
                console.log(response.message);
            }
            $('.loading').css('display', 'none');
        }
    });
}

function getOrdersData() {
    $('.loading').css('display', 'table');
    $.ajax({
        url: '/mis/order/history/getOrderProductCount?keyword=' + searchKeyword + '&date=' + orderDate +
            '&is_urgent=' + isUrgent + '&see_all=' + seeAll + '&is_today=' + isToday + '&page=' + currentPage +
            '&item_range=' + itemRange + '&sort=' + sortBy + '&order=' + orderBy,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                count = Number(jsonObject);
                $("#totalOrders").text(addCommas(count));
                totalPage = Math.ceil(count / itemRange);
                createPagination(currentPage, totalPage);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $("#totalOrders").text(0);
                totalPage = Math.ceil(count / itemRange);
                createPagination(currentPage, totalPage);
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}

function getOrderProducts(orderSeq) {
    $('.loading').css('display', 'table');
    $.ajax({
        url: '/mis/order/history/getOrderProducts?orderSeq=' + orderSeq,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            console.log(response);
            if (response.code == '200') {
                const jsonObject = response.object;
                var thead = '<th>발주상품번호</th>' +
                    '<th>상품ID</th>' +
                    '<th>상품명</th>' +
                    '<th>단가(+VAT)(원)</th>' +
                    '<th>박스단위</th>' +
                    '<th>수량</th>' +
                    '<th>배송비(원)</th>' +
                    '<th>소계(원)</th>' +
                    '<th>발송여부</th>' +
                    '<th>수정</th>' +
                    '<th>삭제</th>';

                var tbody = '';
                $.each(jsonObject, function(i, object) {
                    orderNoteOpen[object.detail_seq] = 0;
                    var formattedOrderDetailSeq = object.detail_seq.substr(0, 6) + '-' + object.detail_seq.substr(6, 10);

                    var formattedUnitPrice = addCommas(object.unit_price.toString());
                    var formattedOrderQty = addCommas(object.order_qty.toString());
                    var formattedShippingFee = addCommas(object.shipping_fee.toString());
                    var formattedLineTotal = addCommas(object.line_total.toString());

                    var formattedTrackShipment = object.has_shipment_validated === 1 ? '<i class="dd-icon shipping-truck-green"></i>' : '<i class="dd-icon shipping-truck-red"></i>'
                    var formattedOrderNote = object.order_note == null ?
                        '<input class="order-note-input" type="text" placeholder="' + '발주 메모가 없습니다. 새로 입력하세요.' + '" value=""></input>' :
                        '<input class="order-note-input" type="text" value="' + object.order_note + '"></input>';
                    var formattedOrderNoteLength = object.order_note == null ? 0 : getTextLength(object.order_note);

                    tbody += '<tr data-order-detail-seq="' + object.detail_seq + '">' +
                        '<td class="order-detail-seq">' + formattedOrderDetailSeq + '</td>' +
                        '<td class="product-id">' + object.id + '</td>' +
                        '<td class="product-name">\
                                <a href="/md-edit-product?product_id=' + object.id + '">' + object.name + '</a>' +
                        '</td>' +
                        '<td class="unit-price">' +
                        '<input class="unit-price-input" style="width: 80%;" type="text" value="' + addCommas(formattedUnitPrice) + '" size=' + getTextLength(formattedUnitPrice) + '></td>' +
                        '<td class="unit-box" >' + object.unit_box + '</td>' +
                        '<td class="order-qty" >' +
                        '<input class="order-qty-input" style="width: 80%;" type="text" value="' + addCommas(formattedOrderQty) + '" size=' + getTextLength(formattedOrderQty) + '></td>' +
                        '<td class="shipping_fee">' +
                        '<input class="shipping-fee-input" style="width: 80%;" type="text" value="' + addCommas(formattedShippingFee) + '" size=' + getTextLength(formattedShippingFee) + '></td>' +
                        '<td class="line-total">' + addCommas(formattedLineTotal) + '</td>' +
                        '<td class="ship-status">' + formattedTrackShipment + '</td>' +
                        '<td class="update-button"><input class="update-order-btn button primary" type="button" value="수정"></td>' +
                        '<td class="delete-button"><input class="delete-order-seq-btn button primary" type="button" value="X"></td>' +
                        '</tr>' +
                        '<tr class="order-note">' +
                        '<td colspan="1" class="order-note-title">발주 메모: </td>' +
                        '<td colspan="4" class="order-note-field">' +
                        formattedOrderNote +
                        '</td>' +
                        '<td class="order-note-bytes" colspan="1">' +
                        formattedOrderNoteLength + '/100 자' +
                        '</td>' +
                        '<td colspan="1">' +
                        '<input type="button" class="update-order-note-btn button primary" value="수정">' +
                        '</td>' +
                        '<td colspan="6"> ' +
                        '<div class="order-note-message"><p class="order-note-msg"></p></div></td>' +
                        '</tr>';
                });

                var head = $(".hidden[data-order-seq-list='" + orderSeq + "'] thead");
                var body = $(".hidden[data-order-seq-list='" + orderSeq + "'] tbody");
                $(head).html(thead);
                $(body).html(tbody);

                $('.order-note-input').on("keypress", function(e) {
                    $(this).parent().next(".order-note-bytes").text(getTextLength($(this).val()) + '/100 자');
                    if (e.which < 0x20) {
                        return;
                    }
                    if (getTextLength($(this).val()) == 100) {
                        e.preventDefault();
                    } else if (getTextLength($(this).val()) > 100) {
                        $(this).val($(this).val().substring(0, 100));
                    }
                });

            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                var tr = '<tr class="result-message">\
                            <td colspan="19">\
                                검색 결과가 없습니다.\
                            </td>\
                        </tr>';
                $(head).html(tr);
            } else {
                console.log(response.message);
            }
            $('.loading').css('display', 'none');
        }
    });
}

function uppdateOrderedProducts(orderDetailSeq, orderProductQty, unitPrice, shippingFee) {
    var dataSet = {
        detail_seq: orderDetailSeq,
        order_qty: Number(orderProductQty.replace(/[^0-9.-]+/g, "")),
        unit_price: Number(unitPrice.replace(/[^0-9.-]+/g, "")),
        shipping_fee: Number(shippingFee.replace(/[^0-9.-]+/g, ""))
    };
    console.log(dataSet);
    $.ajax({
        url: '/mis/order/history/updateOrderedProducts',
        type: 'POST',
        data: JSON.stringify(dataSet),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                // 데이터 처리 완료 알림.
                $(".notification-msg").html('<i class="fas fa-check-circle"></i>' + orderDetailSeq + ' 의 갱신이 정상 처리 되었습니다.').removeClass("warning").addClass("success");
                $(".notification-msg").fadeIn(100).delay(3000).fadeOut(100);

                // window.location.reload(true);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>' + orderDetailSeq + ' 데이터 갱신에 실패했습니다.').removeClass("success").addClass("warning");
                $(".notification-msg").fadeIn(100).delay(3000).fadeOut(100);
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
            getOrdersData();
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}

function updateOrderNote(orderSeqDetail, productId, orderNote) {
    var dataSet = {
        "detail_seq": orderSeqDetail,
        "id": productId,
        "order_note": orderNote
    };
    console.log(dataSet);
    $.ajax({
        url: '/mis/order/history/updateOrderNote',
        type: 'POST',
        data: JSON.stringify(dataSet),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            console.log(response);
            if (response.code == '200') {
                $("[data-order-detail-seq=" + orderSeqDetail + "] + .order-note .order-note-msg").html('<i class="fas fa-check-circle"></i> 주문 메모 정상 갱신 완료.').removeClass("warning").addClass("success");
                $("[data-order-detail-seq=" + orderSeqDetail + "] + .order-note .order-note-message").fadeIn(100).delay(2000).fadeOut(100);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $("[data-order-detail-seq=" + orderSeqDetail + "] + .order-note .order-note-msg").html('<i class="fas fa-exclamation-circle"></i> 주문 메모 처리 실패.').removeClass("success").addClass("warning");
                $("[data-order-detail-seq=" + orderSeqDetail + "] + .order-note .order-note-message").fadeIn(100).delay(2000).fadeOut(100);
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}

function deleteOrder(orderSeq) {
    var dataSet = {
        "order_seq": orderSeq
    };
    $.ajax({
        url: '/mis/order/history/deleteOrder',
        type: 'POST',
        data: JSON.stringify(dataSet),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                $('[data-order-seq= "' + orderSeq + '"] .update-order-btn').parent().parent().remove();
                $(".notification-msg").html('<i class="fas fa-check-circle"></i>' + orderSeq + ' 의 삭제가 정상 처리 되었습니다.').removeClass("warning").addClass("success");
                $(".notification-msg").fadeIn(100).delay(3000).fadeOut(100);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>이미 존재하지 않는 데이터 입니다.').removeClass("success").addClass("warning");
                $(".notification-msg").fadeIn(100).delay(3000).fadeOut(100);
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
            getOrdersData();
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}

function deleteOrderProduct(orderDetailSeq) {
    var dataSet = {
        "detail_seq": orderDetailSeq
    };
    $.ajax({
        url: '/mis/order/history/deleteOrderProduct',
        type: 'POST',
        data: JSON.stringify(dataSet),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                $('[data-order-seq= "' + orderDetailSeq + '"] .update-order-btn').parent().parent().remove();
                $(".notification-msg").html('<i class="fas fa-check-circle"></i>' + orderDetailSeq + '은(는) 정상 삭제되었습니다.').removeClass("warning").addClass("success");
                $(".notification-msg").fadeIn(100).delay(3000).fadeOut(100);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>이미 존재하지 않는 데이터 입니다.').removeClass("success").addClass("warning");
                $(".notification-msg").fadeIn(100).delay(3000).fadeOut(100);
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
            getOrdersData();
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}

function createPagination(current, total) {
    var delta = 1,
        left = parseInt(current) - delta,
        right = parseInt(current) + delta + 1,
        range = [],
        rangeWithDots = [],
        l;

    for (let i = 1; i <= parseInt(total); i++) {
        if (i == 1 || i == parseInt(total) || i >= left && i < right) {
            range.push(i);
        }
    }

    for (let i of range) {
        if (l) {
            if (i - l === 2) {
                rangeWithDots.push(l + 1);
            } else if (i - l !== 1) {
                rangeWithDots.push('...');
            }
        }
        rangeWithDots.push(i);
        l = i;
    }
    console.log("current page:" + current)
    currentPage = current;
    getOrderList();

    var pageTable = "<ul class='page-list'>";
    pageTable += "<li onclick='createPagination(" + '"1","' + totalPage + '"' + ");'>&lt;&lt;</li>";

    for (var pageCell of rangeWithDots) {
        if (pageCell != '...') {
            pageTable += '<li onclick="createPagination(' + "'" + pageCell + "','" + totalPage + "'" + ');">' + pageCell + '</li>';
        } else {
            pageTable += '<li>' + pageCell + '</li>';
        }
    }

    pageTable += "</li><li onclick='createPagination(" + '"' + totalPage + '","' + totalPage + '"' + ");'>&gt;&gt;</li>";
    document.getElementById("pagination").innerHTML = pageTable;

    var aTags = document.getElementsByTagName("li");
    var found;

    for (var i = 0; i < aTags.length; i++) {
        if (aTags[i].textContent == parseInt(current)) {
            found = aTags[i];
            found.classList.add("active");
            break;
        }
    }
}

function getshipmentTrackingBySeq(orderSeq) {
    var data = {
        keyType: "orderSeq",
        seq: orderSeq
    };
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/mfc/shipment/getshipmentTrackingBySeq',
        data: JSON.stringify(data),
        dataType: "json",
        success: function(response) {
            if (response.code == "200") {
                let shipmentStatus = {};
                const jsonObject = response.object;
                console.log(response.object);
                var tr = '';
                $.each(jsonObject, function(i, object) {
                    // var productSkuPerTracking = object.products.length;
                    // console.log(productSkuPerTracking);
                    var carrier_name = object.carrier;
                    var tracking_no = object.tracking_no;

                    // var carrier = object.carrier_site === undefined ? carrier_name : '<a href="' + object.carrier_site + '">' + carrier_name + "</a>"
                    // var tracking = object.tracking_url === undefined ? tracking_no : '<a href="' + object.tracking_url + '">' + tracking_no + "</a>"
                    var trackingUrl = object.urls.tracking === null ? tracking_no : '<a href="' + object.urls.tracking + '">' + tracking_no + "</a>"
                    var officialUrl = object.urls.official === null ? carrier_name : '<a href="' + object.urls.official + '">' + carrier_name + "</a>"

                    shipmentStatus[object.urls.tracking] = [carrier_name, tracking_no];

                    $.each(object.products, function(i, object) {
                        tr += '<tr id="' + object.order_detail_seq + '">' +
                            '<td class="carrier">' + officialUrl + '</td>' +
                            '<td class="tracking-no">' + trackingUrl + '</td>' +
                            '<td class="order-seq">' + object.order_seq + '</td>' +
                            '<td class="order-detail-seq" >' + object.order_detail_seq.substr(6, 4) + "-" + object.product_id + '</td>' +
                            '<td class="product-name"><a href="/md-edit-product?product_id=' + object.product_id + '">' + object.product_name + '</a></td>' +
                            '<td class="status-' + tracking_no + '">.</td>' +
                            '<td class="status-loc-' + tracking_no + '">.</td>' +
                            '<td class="status-date-' + tracking_no + '">.</td>' +
                            '</tr>';
                    });
                });
                // already filtered for its duplicatable values
                $.each(shipmentStatus, function(key, value) {
                    getShipmentStatus({ url: key, carrier: value[0], trackingNo: value[1] });
                });

                $("#tracking-modal-content-body").html(tr);
                mergeTableCells("#tracking-info-table");

            } else {
                var tr = '<tr><td class="no-product" colspan="8">No records found.</td></tr>';
                $("#tracking-modal-content-body").html(tr);
            }
            openTrackShipmentModal();
        },
        error: function(response) {
            var tr = '<tr><td class="no-product" colspan="8">No records found.</td></tr>';
            $("#tracking-modal-content-body").html(tr);
            openTrackShipmentModal();
        }
    });
}

function getShipmentStatus(shipmentData) {
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/mfc/shipment/getShipmentStatus',
        data: JSON.stringify(shipmentData),
        dataType: "json",
        success: function(response) {
            console.log(response)
            const object = response.object;
            if (object.status !== '미발송') {
                $('.status-' + shipmentData['trackingNo']).text(object.status);
                $('.status-loc-' + shipmentData['trackingNo']).text(object.location);
                $('.status-date-' + shipmentData['trackingNo']).text(object.datetime);
            } else {
                $('.status-' + shipmentData['trackingNo']).text(object.status);
                $('.status-loc-' + shipmentData['trackingNo']).text('');
                $('.status-date-' + shipmentData['trackingNo']).text('');
            }

        },
        error: function(response) {
            console.log(response)
        }
    });
}

Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
    var weekKorName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var weekKorShortName = ["일", "월", "화", "수", "목", "금", "토"];
    var weekEngName = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var weekEngShortName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    var d = this;

    return f.replace(/(yyyy|yy|MM|dd|KS|KL|ES|EL|HH|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy":
                return d.getFullYear(); // 년 (4자리)
            case "yy":
                return (d.getFullYear() % 1000).zf(2); // 년 (2자리)
            case "MM":
                return (d.getMonth() + 1).zf(2); // 월 (2자리)
            case "dd":
                return d.getDate().zf(2); // 일 (2자리)
            case "KS":
                return weekKorShortName[d.getDay()]; // 요일 (짧은 한글)
            case "KL":
                return weekKorName[d.getDay()]; // 요일 (긴 한글)
            case "ES":
                return weekEngShortName[d.getDay()]; // 요일 (짧은 영어)
            case "EL":
                return weekEngName[d.getDay()]; // 요일 (긴 영어)
            case "HH":
                return d.getHours().zf(2); // 시간 (24시간 기준, 2자리)
            case "hh":
                return ((h = d.getHours() % 12) ? h : 12).zf(2); // 시간 (12시간 기준, 2자리)
            case "mm":
                return d.getMinutes().zf(2); // 분 (2자리)
            case "ss":
                return d.getSeconds().zf(2); // 초 (2자리)
            case "a/p":
                return d.getHours() < 12 ? "오전" : "오후"; // 오전/오후 구분

            default:
                return $1;
        }
    });
};


String.prototype.string = function(len) {
    var s = '',
        i = 0;
    while (i++ < len) { s += this; }
    return s;
};
String.prototype.zf = function(len) { return "0".string(len - this.length) + this; };
Number.prototype.zf = function(len) { return this.toString().zf(len); };

// tool functions
function getTextLength(str) {
    var len = 0;
    for (var i = 0; i < str.length; i++) {
        if (escape(str.charAt(i)).length == 6) {
            len++;
        }
        len++;
    }
    return len;
}

function addCommas(input) {
    var parts = input.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

function mergeTableCells(x) {
    $(x).each(function() {
        $(x).find('td').each(function() {
            var $this = $(this),
                y = $this.index(),
                z = $this.html(),
                rs = 1,
                tc = $($this.parent().prev().children()[y]);

            while (tc.html() === z) {
                rs += 1;
                tc_old = tc;
                tc = $(tc.parent().prev().children()[y]);
            }

            if (rs > 1) {
                $(tc_old).attr('rowspan', rs);
                $this.hide();
            }
        });
    });
}

// visibility functions

function closeTrackShipmentModal() {
    $(".tracking-info-modal").css("visibility", "hidden");
    $(".tracking-info-modal").css("opacity", "0");
}

function openTrackShipmentModal() {
    $(".tracking-info-modal").css("visibility", "visible");
    $(".tracking-info-modal").css("opacity", "1");
}