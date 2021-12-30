var sortBy;
var orderBy;
var currentPage;
var itemRange;
var searchKeyword;
var transactionRow;
var datePlaceHolder;
var clickedOrderSeq;
var isUrgent = 0;

$(document).ready(function() {
    $(".edit-btn").on("click", function() {
        var md_order_id = $(this).attr("data-id");

        var formData = {
            md_order_seq: md_order_id
        }

        $.ajax({
            url: '/getOneTransaction',
            type: 'POST',
            data: JSON.stringify(formData),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success: function(response) {
                if (response.code == '200') {
                    console.log(JSON.stringify(response.object));
                    // GET MD ORDER LIST
                    var orderDetailObject = response.object.mdOrderDetaiList;

                    // Loop through md order list
                    var order_table = '';
                    $.each(orderDetailObject, function(i, object) {
                        order_table += '<tr id="' + object.seq + '"><td><input type="hidden" name="product_id" value="' + object.product_id + '">' +
                            object.product_name + '</td><td>' +
                            '<input type="number" name="order_qty" value="' + object.order_qty + '"></td><td>' +
                            '<input type="number" name="unit_price" value="' + object.unit_price + '"></td><td>' +
                            '<input type="number" name="shipping_fee" value="' + object.shipping_fee + '"></td><td>' +
                            '<input type="hidden" name="order_price" value="' + object.order_price + '">' + object.order_price + '</td><td>' +
                            '<input type="submit" class="button primary update-btn" data-seq="' + object.seq + '" data-md-seq="' + object.md_order_seq + '" value="수정"></td><td>' +
                            '<input type="submit" class="button greyed delete-btn" data-seq="' + object.seq + '" data-md-seq="' + object.md_order_seq + '" value="삭재"></td></tr>';
                    });

                    // Get order id and append order details
                    var orderDetailID = $(".order-detail[data-id='" + md_order_id + "'] tbody");
                    $(orderDetailID).html(order_table);

                    // Get order id and show thead
                    var orderDetailthead = $(".order-detail[data-id='" + md_order_id + "'] thead");
                    $(orderDetailthead).removeClass('hide');
                    $(orderDetailthead).addClass('show');

                    // Get order id and add highlight
                    var orderDetailhighlight = $(".main-row-" + md_order_id + "");
                    $(orderDetailhighlight).addClass('highlight');
                    $(".edit-btn").addClass('hide');
                    $(".edit-btn").removeClass('show');
                    $(".close-btn").addClass('show');
                    $(".close-btn").removeClass('hide');

                    // GET TRANSACTION LIST
                    var orderTransactionObject = response.object.mdTransactionList;
                    //console.log(orderTransactionObject, 'orderTransactionObject');

                    // Loop through md transaction list
                    var transaction_table = '';
                    $.each(orderTransactionObject, function(i, object) {
                        transaction_table += '<tr class="transaction" id="' + object.seq + '"><td><input type="hidden" name="seq" value="' + object.seq + '"><input type="hidden" name="md_order_seq" value="' + object.md_order_seq + '">' +
                            '<input type="text" name="date" value="' + object.date + '"></td><td>' +
                            '<input type="number" name="amount" class="compute" value="' + object.amount + '"></td><td>' +
                            '<input type="number" name="amount-vat" class="compute" value="' + object.amount_vat + '"></td><td>' +
                            '<input type="text" name="comp_bank" value="' + object.comp_bank + '"></td><td>' +
                            '<input type="text" name="comp_account_holder" value="' + object.comp_account_holder + '"></td><td>' +
                            '<input type="text" name="comp_account" value="' + object.comp_account + '"></td><td>' +
                            '<input type="text" name="create_user" value="' + object.create_user + '" disabled></td><td>' +
                            '<input type="text" name="issue" value="' + object.issue + '"></td><td>' +
                            '</td></tr>'
                    });

                    // Get order id and append order transaction
                    var orderTransactionID = $(".order-transaction[data-id='" + md_order_id + "'] tbody");
                    $(orderTransactionID).html(transaction_table);

                    // Get order id and show thead
                    var orderTransactionthead = $(".order-transaction[data-id='" + md_order_id + "'] thead");
                    $(orderTransactionthead).removeClass('hide');
                    $(orderTransactionthead).addClass('show');

                    // TRANSACTION TFOOT

                    // Calculate transaction total
                    //$('.compute').on('input', function () {
                    $("body").on("input", ".compute", function() {
                        var transaction_sum = 0;
                        $('.transaction').each(function() {
                            var transaction_fields = $(this).find("[name='amount']").val();
                            transaction_sum += parseFloat(transaction_fields);
                        });
                        $('.transaction-sum').html(transaction_sum);
                        // console.log(transaction_sum);
                    });

                    // Add transaction tfoot
                    var transaction_table_foot = '<tr style="text-align: center; background: #f9f9f9;"><td>' +
                        '총 액</td><td>' +
                        '<span class="transaction-sum"></span></td><td colspan="4">' +
                        '</td><td>' +
                        '<input type="submit" class="button primary t-add-btn" value="Add"></td><td>' +
                        '<input type="submit" class="button primary t-update-btn" value="Update"></td></tr>';

                    // Get order id and append order transaction tfoot
                    var orderTransactionIDfoot = $(".order-transaction[data-id='" + md_order_id + "'] tfoot");
                    $(orderTransactionIDfoot).html(transaction_table_foot);


                    // Cancel Edit Btn
                    $("body").on("click", ".close-btn", function() {
                        $(orderDetailID).empty();
                        $(orderDetailthead).removeClass('show');
                        $(orderDetailthead).addClass('hide');
                        $(orderDetailhighlight).removeClass('highlight');
                        $(orderTransactionthead).removeClass('show');
                        $(orderTransactionthead).addClass('hide');
                        $(orderTransactionIDfoot).empty();
                        $(".edit-btn").addClass('show');
                        $(".edit-btn").removeClass('hide');
                        $(".close-btn").addClass('hide');
                        $(".close-btn").removeClass('show');
                    });

                } else {

                }
            }
        }); // end getOneTransaction
    });

    // Update Transaction
    $("body").on("click", ".t-update-btn", function() {
        var rowcount = $('.transaction').length;
        var list = [];
        var content = '';

        $(".transaction").each(function(index) {
            var seq = $(this).find("[name='seq']").val();
            var md_order_seq = $(this).find("[name='md_order_seq']").val();
            var date = $(this).find("[name='date']").val();
            var amount = $(this).find("[name='amount']").val();
            var amount_vat = $(this).find("[name='amount-vat']").val();
            var comp_bank = $(this).find("[name='comp_bank']").val();
            var comp_account_holder = $(this).find("[name='comp_account_holder']").val();
            var comp_account = $(this).find("[name='comp_account']").val();
            var issue = $(this).find("[name='issue']").val();

            var content = {
                seq: seq,
                md_order_seq: md_order_seq,
                date: date,
                amount: amount,
                amount_vat: amount_vat,
                comp_bank: comp_bank,
                comp_account_holder: comp_account_holder,
                comp_account: comp_account,
                issue: issue
            };

            list.push(content);
        });

        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/createMdTransaction',
            data: JSON.stringify(list),
            dataType: "json",
            success: function(response) {
                var transactionObj = response.object;
                if (response.code == "200") {
                    alert('success');
                }
            }

        });
    }); //end t-add-btn

    var tempTransactionID = 0;
    // Add Transaction
    $("body").on("click", ".t-add-btn", function() {
        // Display logged in user as create_user
        var getUsername = $("#userID").val().trim();
        tempTransactionID++;

        // Auto fill newly created row with bank, account and holder from the previous transaction row
        var getTransactionRows = $(this).closest('table').children('tbody').children('tr');
        var firstTransaction = getTransactionRows[0];
        var getCompBank = $(firstTransaction).find("[name='comp_bank']").val();
        var getCompAccountHolder = $(firstTransaction).find("[name='comp_account_holder']").val();
        var getCompAccount = $(firstTransaction).find("[name='comp_account']").val();

        if (getTransactionRows.length > 0) {
            // console.log('contains 1 or more rows');

            var add_transaction = '<tr class="transaction" id="temp-' + tempTransactionID + '"><td>' +
                '<input type="text" name="date" value=""></td><td>' +
                '<input type="number" name="amount" class="compute" value=""></td><td>' +
                '<input type="number" name="amount-vat" class="compute" value=""></td><td>' +

                '<input type="text" name="comp_bank" value="' + getCompBank + '"></td><td>' +
                '<input type="text" name="comp_account_holder" value="' + getCompAccountHolder + '"></td><td>' +
                '<input type="text" name="comp_account" value="' + getCompAccount + '"></td><td>' +
                '<input type="text" name="create_user" value="' + getUsername + '" disabled></td><td>' +
                '<input type="text" name="issue" value=""></td><td>' +
                '<input type="submit" class="button greyed t-delete-btn" value="삭재"></td></tr>'

            $(this).closest('table').children('tbody').append(add_transaction);
        } else {
            // console.log('contains 0 rows');

            // If there's no row yet, do create as empty fields
            var add_transaction = '<tr class="transaction" id="temp-' + tempTransactionID + '"><td>' +
                '<input type="text" name="date" value=""></td><td>' +
                '<input type="number" name="amount" class="compute" value=""></td><td>' +
                '<input type="number" name="amount-vat" class="compute" value=""></td><td>' +
                '<input type="text" name="comp_bank" value=""></td><td>' +
                '<input type="text" name="comp_account_holder" value=""></td><td>' +
                '<input type="text" name="comp_account" value=""></td><td>' +
                '<input type="text" name="create_user" value="' + getUsername + '"></td><td>' +
                '<input type="text" name="issue" value=""></td><td>' +
                '<input type="submit" class="button greyed t-delete-btn" value="삭재"></td></tr>'

            $(this).closest('table').children('tbody').append(add_transaction);
        }

        //Cancel Edit Btn
        $("body").on("click", ".close-btn", function() {
            var data_id = $(this).attr("data-id");
            var delete_tr = $(".hidden-row [data-id='" + data_id + "'] tbody tr");
            $(delete_tr).empty();
        });
    }); //end t-add-btn'


    // Delete newly created transaction row
    $("body").on("click", ".t-delete-btn", function() {
        $(this).parent().parent().remove();
    }); //end close-btn'

    currentPage = $("#listPage").val();
    itemRange = $("#itemRange").val();
    searchKeyword = $(".search > input").val();
    sortBy = $("#sortBy").val();
    orderBy = $("#orderBy").val();

    getTransactionListCount(searchKeyword, isUrgent);

    getTransactionList(currentPage, itemRange, searchKeyword, sortBy, orderBy, isUrgent);

    $("#orderDate, #orderSeq, #orderCompany, #orderCost, #orderVat, #orderTotal,\
     #orderPIC, #orderPaymentMethod, #orderPaymentStatus, #orderPaymentDeadline,\
      #orderPDFDate, #orderCompletedDate, #orderUrgency, #billValidation").on("click", function() {
        orderBy = $(this).attr("data-order");
        sortBy = $(this).attr("data-sort");

        if (orderBy == 1) {
            $(this).attr("data-order", 0);
            searchKeyword = $(".search > input").val();
            currentPage = 1;
            $("#listPage").val(currentPage);
            getTransactionListCount(searchKeyword, isUrgent);
            $("#orderBy").val(orderBy);
            $("#sortBy").val(sortBy);
        } else {
            $(this).attr("data-order", 1);
            searchKeyword = $(".search > input").val();
            currentPage = 1;
            $("#listPage").val(currentPage);
            getTransactionListCount(searchKeyword, isUrgent);
            $("#orderBy").val(orderBy);
            $("#sortBy").val(sortBy);
        }
    });

    $(".search > button").on("click", function(event) {
        event.preventDefault();
        searchKeyword = $(".search > input").val();
        currentPage = 1;
        $("#listPage").val(currentPage);
        getTransactionListCount(searchKeyword, isUrgent);
        $("#searchKeyword").val(searchKeyword);
    });

    $(".search > input").keypress(function(e) {
        var key = e.which;
        if (key == 13) {
            currentPage = 1;
            $("#listPage").val(currentPage);
            searchKeyword = $(this).val().trim();
            getTransactionListCount(searchKeyword, isUrgent);
            $("#searchKeyword").val(searchKeyword);
        }
    });

    $("body").on("click", "#show-urgent-list", function() {
        isUrgent = $('#show-urgent-list').is(":checked") ? 1 : 0;
        getTransactionList(currentPage, itemRange, searchKeyword, sortBy, orderBy, isUrgent);
        getTransactionListCount(searchKeyword, isUrgent);

    });

    $('#item_range').change(function() {
        currentPage = 1;
        $("#listPage").val(currentPage);
        itemRange = $(this).val();
        orderBy = $("#orderBy").val();
        $("#itemRange").val(itemRange);
        getTransactionListCount(searchKeyword, isUrgent);
    });

    $("#downloadPDF").click(function() {
        var list = { "order_details": [] };
        if ($("input[name='order-item']:checked").length > 0) {
            $.each($("input[name='order-item']:checked"), function() {
                var order_seq = $(this).val();
                var company_name = $(this).parent().siblings('.comp-name').html();
                var payment_date = $(this).parent().siblings('.payment-deadline').text();
                var biz_no = $(this).parent().siblings('.comp-name').attr('id');
                var transaction_method = $(this).parent().siblings('.transaction-method').text();
                var jsonSeqNo = {
                    'order_seq': order_seq,
                    'comp_name': company_name,
                    'payment_date': payment_date,
                    'biz_no': biz_no,
                    'transaction_method': transaction_method
                };
                list.order_details.push(jsonSeqNo);
            });
            callGenerateMultipleProcessingOrderPDF(list);
        } else {
            $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>처리할 주문서를 선택하세요.').removeClass("success").addClass("warning");
            $(".notification-message").animate({ opacity: 1 }, 500, function() {
                $('.notification-message').css('visibility', 'visible');
                $(".notification-message").animate({ opacity: 0 }, 3000, function() {
                    $('.notification-message').css('visibility', 'hidden');
                });
            });
        }
    });

    $("#downloadExcel").click(function() {
        var list = { "order_details": [] };
        if ($("input[name='order-item']:checked").length > 0) {
            $.each($("input[name='order-item']:checked"), function() {
                var order_seq = $(this).val();
                var company_name = $(this).parent().siblings('.comp-name').html();
                var payment_date = $(this).parent().siblings('.payment-deadline').text();
                var biz_no = $(this).parent().siblings('.comp-name').attr('id');
                var transaction_method = $(this).parent().siblings('.transaction-method').text();
                var jsonSeqNo = {
                    'order_seq': order_seq,
                    'comp_name': company_name,
                    'payment_date': payment_date,
                    'biz_no': biz_no,
                    'transaction_method': transaction_method
                };
                list.order_details.push(jsonSeqNo);
            });
            callGenerateMultipleProcessingOrderExcel(list);
        } else {
            $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>처리할 주문서를 선택하세요.').removeClass("success").addClass("warning");
            $(".notification-message").animate({ opacity: 1 }, 500, function() {
                $('.notification-message').css('visibility', 'visible');
                $(".notification-message").animate({ opacity: 0 }, 3000, function() {
                    $('.notification-message').css('visibility', 'hidden');
                });
            });
        }
    });

    $("#checkAll").click(function() {
        $('input[name="order-item"]').not(this).prop('checked', this.checked);
    });

    $("#generateAll").on("click", function() {
        callGenerateAllProcessingOrderPDF();
    });

    $("body").on("click", ".urgent-payment-chk-box", function() {
        var self = $(this).parent().parent();
        var is_checked = self.find("[name='urgent-cb']").is(":checked");
        var md_order_seq = $(this).val();

        if (is_checked) {
            markUrgentTransaction(1, md_order_seq);
            $(this).parent().parent().addClass("urgent-row");
        } else {
            markUrgentTransaction(0, md_order_seq);
            $(this).parent().parent().removeClass("urgent-row");
        }
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

function reloadCurrentPage() {
    getTransactionList(currentPage, itemRange, searchKeyword, sortBy, orderBy, isUrgent);
    getTransactionListCount(searchKeyword, isUrgent);
    totalPage = Math.ceil(count / itemRange);
    createPagination(currentPage, totalPage);
}

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
            $(".notification-message").animate({ opacity: 1 }, 500, function() {
                $('.notification-message').css('visibility', 'visible');
                $(".notification-message").animate({ opacity: 0 }, 3000, function() {
                    $('.notification-message').css('visibility', 'hidden');
                });
            });
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


function getTransactionList(page, item_range, keyword, sort, order, is_urgent) {
    $.ajax({
        url: '/finance/getOrderTransactionList?page=' + page + '&item_range=' +
            item_range + '&keyword=' + keyword + '&sort=' + sort + '&order=' +
            order + '&is_urgent=' + is_urgent,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                var tr = '';

                if (Object.keys(jsonObject).length > 0) {
                    $.each(jsonObject, function(i, object) {
                        var formattedOrderSeq = "HPOF-" + object.md_order_seq.substr(0, 6) + '-' + object.md_order_seq.substr(6, 10);
                        var formattedSubTotal = object.subtotal.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                        var formattedVat = object.vat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                        var formattedTotalAmount = object.total.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                        var statusClass = object.payment_status == "입금 대기" ? "incomplete" : object.payment_status == "입금 중" ? "less" : object.payment_status == "입금 완료" ? "completed" : object.payment_status == "입금 초과" ? "exceeded" : "";
                        var transactionClass = object.evid_type == "세금 계산서" ? "tax-invoice" : object.evid_type == "면세 계산서" ? "bill" : object.evid_type == "예치금(면세)" ? "deposit-no-tax" : object.evid_type == "예치금(과세)" ? "deposit-with-tax" : object.evid_type == "법인카드" ? "corporate" : "unknown";

                        var urgentCheckBox = object.is_urgent_transaction == 0 ? "" : "checked";
                        var billValidationCheckBox = object.is_bill_validated == 0 ? "" : "checked";
                        var dateClass = "";
                        var inputDate = new Date(object.pay_deadline);
                        var todaysDate = new Date();

                        "입금 완료" == object.payment_status ? dateClass = "" : inputDate.setHours(0, 0, 0, 0) == todaysDate.setHours(0, 0, 0, 0) ? dateClass = "date-orange" : inputDate.setHours(0, 0, 0, 0) < todaysDate.setHours(0, 0, 0, 0) ? dateClass = "date-red" : dateClass = "date-green";

                        var pdfDate = '';
                        if (object.pdf_generate_date != null && object.pdf_generate_date != '') {
                            pdfDate = object.pdf_generate_date
                            var date = new Date(pdfDate);
                            pdfDate = [
                                date.getFullYear(),
                                ('0' + (date.getMonth() + 1)).slice(-2),
                                ('0' + date.getDate()).slice(-2)
                            ].join('-');
                        }
                        var paymentCompletionDate = '';
                        if (object.latest_payment_date != null && object.latest_payment_date != '') {
                            paymentCompletionDate = object.latest_payment_date
                            var date = new Date(paymentCompletionDate);
                            paymentCompletionDate = [
                                date.getFullYear(),
                                ('0' + (date.getMonth() + 1)).slice(-2),
                                ('0' + date.getDate()).slice(-2)
                            ].join('-');
                        }
                        tr += '<tr data-order-seq="' + object.md_order_seq + '">' +
                            '<td class="chk-box"><input type="checkbox" name="order-item" value="' + object.md_order_seq + '">' +
                            '<td class="order-date">' + object.order_date + '</td>' +
                            '<td class="order-seq">' + formattedOrderSeq + '</td>' +
                            '<td class="comp-name" id="' + object.comp_id + '" data-bank="' + object.bank + '" data-account="' + object.account + '" data-account-holder="' + object.account_holder + '">' + object.comp_name + '</td>' +
                            '<td class="subtotal">' + formattedSubTotal + '</td>' +
                            '<td class="vat">' + formattedVat + '</td>' +
                            '<td class="grand-total">' + formattedTotalAmount + '<input type="hidden" value="' + object.total + '"></td>' +
                            '<td class="hanpoom-pic">' + object.hanpoom_pic + '</td>' +
                            '<td class="transaction-method"><span class="' + transactionClass + '">' + object.evid_type + '</span></td>' +
                            '<td class="payment-status"><span class="' + statusClass + '">' + object.payment_status + '</span></td>' +
                            '<td class="payment-deadline"><span class="' + dateClass + '">' + object.pay_deadline + '</span></td>' +
                            '<td class="pdf-date">' + pdfDate + '</td>' +
                            '<td class="payment-completion">' + paymentCompletionDate + '</td>' +
                            '<td class="urgent-payment"><input class="urgent-payment-chk-box" value="' + object.md_order_seq + '"\
                                type="checkbox" name="urgent-cb" ' + urgentCheckBox + '></td>' +
                            '<td class="bill-validation"><input class="bill-validation-chk-box" value="' + object.md_order_seq + '"\
                                type="checkbox" name="bill-validation-cb" disabled ' + billValidationCheckBox + '></td>' +
                            '<td width="100"><a class="edit-btn">Edit</a><a class="close-btn hide"><i class="material-icons-outlined">close</i></a><a class="generate-pdf"' +
                            'onclick="generatePDF(' + "'" + object.md_order_seq + "','" + object.comp_name + "','" + object.evid_type + "','" + object.pay_deadline + "'" + ');">PDF</a><a class="save-btn hide">Save</a></td>' +
                            '</tr><tr class="hidden" data-orderlist="' + object.md_order_seq + '"><td colspan="16"><table><thead></thead><tbody></tbody></table></td>' +
                            '</tr><tr class="hidden" data-transaction="' + object.md_order_seq + '"><td colspan="16"><table><thead></thead><tbody></tbody><tfoot></tfoot></table></td></tr>';
                    });
                } else {
                    tr = '<tr><td colspan="18" style="text-align: center;font-weight: 600;color: #ff3b2c;font-size: 18px;">No results found.</td></tr>';
                }
                $('tbody#transactionList').html(tr);
                // $("tbody#transactionList tr").click(function(c) {
                //     if (!$(c.target).is(":checkbox")) {
                //         var i = $(this).find(":checkbox");
                //         i.prop("checked", !i.is(":checked"))
                //     }
                // });

                $(".edit-btn").on("click", function() {
                    var order_seq = $(this).parent().parent().attr("data-order-seq");
                    var comp_details = {
                        bank: $(this).parent().siblings(".comp-name").data("bank"),
                        account: $(this).parent().siblings(".comp-name").data("account"),
                        account_holder: $(this).parent().siblings(".comp-name").data("account-holder")
                    }
                    var payment_deadline = $(this).parent().siblings(".payment-deadline").children("span").text();
                    datePlaceHolder = $(this).parent().siblings(".payment-deadline").html();
                    var input_date = '<input type="date" class="payment-deadline" name="payment_deadline" value="' + payment_deadline + '">';
                    // console.log(payment_deadline);
                    getOrderTransactionDetail(order_seq);
                    getTransactionsPerOrder(order_seq, comp_details);
                    $(this).parent().parent().addClass("highlight");
                    //$(this).parent().find('.close-btn').addClass('show');
                    $(this).parent().find('.close-btn').removeClass('hide');
                    $('.edit-btn').addClass('hide');
                    $('.edit-btn').removeClass('show');
                    $(this).parent().siblings(".payment-deadline").html(input_date);
                    $(this).siblings(".save-btn").removeClass("hide");
                    $(this).siblings(".generate-pdf").addClass("hide");
                    //clickedOrderSeq = "";
                });

                $(".save-btn").on("click", function() {
                    var order_seq = $(this).parent().parent().data("order-seq")
                    var payment_deadline = $(this).parent().siblings(".payment-deadline").children("input[name='payment_deadline']").val();

                    var formData = {
                        md_order_seq: order_seq,
                        pay_deadline: payment_deadline
                    };

                    $.ajax({
                        type: 'POST',
                        contentType: 'application/json',
                        url: '/finance/updatePaymentDeadline',
                        data: JSON.stringify(formData),
                        dataType: 'json',
                        encode: true,
                        success: function(response) {
                            if (response.code == '200') {
                                getTransactionListCount(searchKeyword, isUrgent);
                            } else {
                                alert('Invalid');
                            }
                        }
                    });
                    getTransactionListCount(searchKeyword, isUrgent);
                });

                //if (clickedOrderSeq != "" && clickedOrderSeq != null) {
                //    var x = clickedOrderSeq;
                //    $("tr[data-order-seq='" + x + "']").find("td > a.edit-btn").click();
                //clickedOrderSeq= "";
                //}

                $.each($("input[name='urgent-cb']:checked"), function() {
                    $(this).parent().parent().addClass("urgent-row");
                });

                $.each($("input[name='bill-validation-cb']:checked"), function() {
                    $(this).parent().parent().addClass("bill-validation-row");
                });

            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
        },
        error: function(x, e) {
            if (x.status == 404) {
                console.log("데이터를 정상적으로 불러오지 못했습니다.");
            }
        }
    });
}

function getOrderTransactionDetail(orderseq) {
    $.ajax({
        url: '/finance/getOrderTransactionDetail?order_seq=' + orderseq,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                var tbody = '';
                var thead = '<th>상품명</th><th>Qty</th><th>단가 (원)</th><th>배송비 (원)</th><th>상품 발주가 (원)</th>';
                $.each(jsonObject, function(i, object) {
                    tbody += '<tr data-order-seq="' + object.md_order_seq + '">' +
                        '<td class="product-name">' + object.product_name + '</td>' +
                        '<td class="order-qty">' + object.order_qty + '</td>' +
                        '<td class="order-price">' + object.supply_price + '</td>' +
                        '<td class="shipping-fee">' + object.shipping_fee + '</td>' +
                        '<td class="unit-price">' + object.order_cost + '</td>' +
                        '</tr>';
                });
                var head = $(".hidden[data-orderlist='" + orderseq + "'] thead");
                var body = $(".hidden[data-orderlist='" + orderseq + "'] tbody");
                $(head).html(thead);
                $(body).html(tbody);

                $("body").on("click", ".close-btn", function() {
                    $(head).empty();
                    $(body).empty();
                    //$('.edit-btn').addClass('show');
                    $('.edit-btn').removeClass('hide');
                    $(this).addClass('hide');
                    $(this).removeClass('show');
                    $(this).closest('.save-btn').addClass('hide');
                    $(this).parent().parent().removeClass('highlight');
                    $(this).parent().siblings(".payment-deadline").html(datePlaceHolder);
                    $(this).siblings(".save-btn").addClass("hide");
                    $(this).siblings(".generate-pdf").removeClass("hide");
                    //clickedOrderSeq = "";
                });
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
        },
        error: function() {
            console.log("error");
        }
    });
}

function getTransactionsPerOrder(orderseq, comp_details) {
    $.ajax({
        url: '/finance/getTransactionsPerOrder?order_seq=' + orderseq,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code = '200') {
                const jsonObject = response.object;
                // console.log(jsonObject);
                var thead = '<th>입금 일자</th> \
                                <th>공급가액</th>\
                                <th>부가세액</th>\
                                <th>은행</th>\
                                <th>소유주</th>\
                                <th>계좌 번호</th>\
                                <th>담당자</th>\
                                <th>비고</th>\
                                <th></th>\
                                <th></th>';
                var tbody = '';
                //var tfoot = '<td colspan="5"></td><td>처리 일자</td><td>수정 일자</td><td></td><td></td>';
                var totalVat = 0;
                var totalSubtotal = 0;

                var grandVat = 0;
                var grandSubtotal = 0;

                grandVat = Number($("[data-order-seq='" + orderseq + "'] .vat").text().replace(/,/gi, ""));
                grandSubtotal = Number($("[data-order-seq='" + orderseq + "'] .subtotal").text().replace(/,/gi, ""));
                if (jsonObject[0] != null) {

                    $.each(jsonObject, function(i, object) {
                        totalVat += Number(object.amount_vat);
                        totalSubtotal += Number(object.amount);

                        tbody += '<tr class="t" data-order-seq="' + object.seq + '">' +
                            '<td class="date"><input type="date" name="date" value="' + object.trans_date + '" disabled></td>' +
                            '<td class="amount"><input name="amount" type="text" value="' + object.amount + '" disabled></td>' +
                            '<td class="amoun-vat"><input name="amount-vat" type="text" value="' + object.amount_vat + '" disabled></td>' +
                            '<td class="comp-bank"><input name="comp-bank" type="text" value="' + object.comp_bank + '" disabled></td>' +
                            '<td class="comp-account-holder"><input name="comp-account-holder" type="text" value="' + object.comp_account_holder + '" disabled></td>' +
                            '<td class="comp-account"><input name="comp-account" type="text" value="' + object.comp_account + '" disabled></td>' +
                            '<td class="create-user">' + object.create_user + '</td>' +
                            '<td class="issue"><input name="issue" type="text" value="' + object.issue + '" disabled></td>' +
                            '<td width="100"><a class="close-btn-row hide">Cancel</a><a class="edit-btn-row">Edit</a><a class="delete-btn-row">Delete</a></td>' +
                            '<td width="100"><input name="edit-transaction" type="button" class="button primary" value="Save" data-order-seq="' + object.seq + '"></td>'
                        '</tr>';
                    });
                }
                var head = $(".hidden[data-transaction='" + orderseq + "'] thead");
                var body = $(".hidden[data-transaction='" + orderseq + "'] tbody");
                var foot = $(".hidden[data-transaction='" + orderseq + "'] tfoot");
                $(head).html(thead);
                $(body).html(tbody);
                //$(foot).html(tfoot);

                $("body").on("click", ".close-btn", function() {
                    $(head).empty();
                    $(body).empty();
                    $(foot).empty();
                });

                var rowcount = $('.t').length;
                if (rowcount != 0 && totalSubtotal < grandSubtotal) {
                    var username = $("#userID").val().trim();
                    var date = new Date().toISOString().slice(0, 10);
                    var diff = Number(grandSubtotal - totalSubtotal);
                    var diff_vat = Number(grandVat - totalVat);
                    var add_transaction = '<tr>' +
                        '<td class="date"><input type="date" name="date" value="' + date + '"></td>' +
                        '<td class="amount"><input name="amount" type="text" value="' + diff + '"></td>' +
                        '<td class="amount-vat"><input name="amount-vat" type="text" value="' + diff_vat + '"></td>' +
                        '<td class="comp-bank"><input name="comp_bank" type="text" value="' + jsonObject[0].comp_bank + '"></td>' +
                        '<td class="comp-account-holder"><input name="comp_account_holder" type="text" value="' + jsonObject[0].comp_account_holder + '"></td>' +
                        '<td class="comp-account"><input name="comp_account" type="text" value="' + jsonObject[0].comp_account + '"></td>' +
                        '<td class="create-user">' + username + '</td>' +
                        '<td class="issue"><input name="issue" type="text" value=""></td>' +
                        '<td><input type="submit" data-order-seq="' + orderseq + '" class="update-btn" value="반영"></td></tr>';
                    $("[data-transaction='" + orderseq + "'] tbody").append(add_transaction);
                } else if (rowcount == 0) {
                    var username = $("#userID").val().trim();
                    var date = new Date().toISOString().slice(0, 10);
                    var diff = Number(grandSubtotal - totalSubtotal);
                    var diff_vat = Number(grandVat - totalVat);
                    var add_transaction = '<tr><td>' +
                        '' + date + '<input type="hidden" name="date" value="' + date + '"></td><td>' +
                        '<input type="number" name="amount" value="' + diff + '"></td><td>' +
                        '<input type="number" name="amount-vat" value="' + diff_vat + '"></td><td>' +
                        '<input type="text" name="comp_bank" value="' + (comp_details.bank == null ? "" : comp_details.bank) + '"></td><td>' +
                        '<input type="text" name="comp_account_holder" value="' + (comp_details.account_holder == null ? "" : comp_details.account_holder) + '"></td><td>' +
                        '<input type="text" name="comp_account" value="' + (comp_details.account == null ? "" : comp_details.account) + '"></td><td>' +
                        '' + username + '</td><td>' +
                        '<input type="text" name="issue" value=""></td><td>' +
                        '<input type="submit" data-order-seq="' + orderseq + '" class="update-btn" value="입금처리"></td></tr>';

                    $("[data-transaction='" + orderseq + "'] tbody").append(add_transaction);
                }
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                console.log(response.message);
            }

            $(".edit-btn-row").on("click", function() {
                $(this).addClass("hide");
                $(this).siblings(".close-btn-row").removeClass("hide");
                $(this).parent().siblings().children("input[type='text']").each(function() {
                    $(this).prop("disabled", false);
                });
                $(this).parent().siblings().children("input[type='date']").prop("disabled", false);
                $(this).parent().siblings().children("input[name='edit-transaction']").show();
            });

            $(".close-btn-row").on("click", function() {
                $(this).addClass("hide");
                $(this).siblings(".edit-btn-row").removeClass("hide");
                $(this).parent().siblings().children("input[type='text']").each(function() {
                    $(this).prop("disabled", true);
                });
                $(this).parent().siblings().children("input[type='date']").prop("disabled", true);
                $(this).parent().siblings().children("input[name='edit-transaction']").hide();
            });

            $("input[name='edit-transaction']").on("click", function() {
                var order_seq = $(this).parent().parent().data("order-seq");
                var date = $(this).parent().siblings().find("[name='date']").val();
                var amount = $(this).parent().siblings().find("[name='amount']").val();
                var amount_vat = $(this).parent().siblings().find("[name='amount-vat']").val();
                var issue = $(this).parent().siblings().find("[name='issue']").val();
                var comp_bank = $(this).parent().siblings().find("[name='comp-bank']").val();
                var comp_account = $(this).parent().siblings().find("[name='comp-account']").val();
                var comp_account_holder = $(this).parent().siblings().find("[name='comp-account-holder']").val();
                var parent_order_seq = $(this).parent().parent().parent().parent().parent().parent().data("transaction");

                var comp_details = {
                    bank: comp_bank,
                    account: comp_account,
                    account_holder: comp_account_holder
                }

                var formData = {
                    seq: order_seq,
                    trans_date: date,
                    amount: amount,
                    amount_vat: amount_vat,
                    issue: issue,
                    comp_bank: comp_bank,
                    comp_account: comp_account,
                    comp_account_holder: comp_account_holder
                };

                $.ajax({
                    type: 'POST',
                    contentType: 'application/json',
                    url: '/finance/updateOrderTransactionRow',
                    data: JSON.stringify(formData),
                    dataType: 'json',
                    encode: true,
                    success: function(response) {
                        if (response.code == '200') {
                            //getTransactionListCount(parent_order_seq, comp_details);
                            //clickedOrderSeq = parent_order_seq;
                            getTransactionListCount(searchKeyword, isUrgent);
                        } else {
                            alert('Invalid');
                        }
                    }
                });
            });

            $(".delete-btn-row").on("click", function() {
                var transaction_seq = $(this).parent().parent().data("order-seq");
                var comp_bank = $(this).parent().siblings().find("[name='comp-bank']").val();
                var comp_account = $(this).parent().siblings().find("[name='comp-account']").val();
                var comp_account_holder = $(this).parent().siblings().find("[name='comp-account-holder']").val();
                var parent_order_seq = $(this).parent().parent().parent().parent().parent().parent().data("transaction");

                var comp_details = {
                    bank: comp_bank,
                    account: comp_account,
                    account_holder: comp_account_holder
                }

                var confirmation = confirm("Are you sure you want to delete this item?");
                if (confirmation == true) {
                    $.ajax({
                        type: 'POST',
                        url: '/finance/deleteOrderTransactionRow?transaction_seq=' + transaction_seq,
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function(response) {
                            if (response.code == '200') {
                                //getTransactionsPerOrder(parent_order_seq, comp_details);
                                //clickedOrderSeq = parent_order_seq;
                                getTransactionListCount(searchKeyword, isUrgent);
                            } else {
                                alert('Invalid');
                            }
                        }
                    });
                }
            });

            $(".update-btn").on("click", function() {
                var order_seq = $(this).attr("data-order-seq");
                insertTransaction(order_seq);
            });
        },
        error: function() {
            console.log("error");
        }
    });
}

function insertTransaction(orderseq) {
    var order_seq_tr = $(".update-btn").parent().parent();
    var date = $(order_seq_tr).find("[name='date']").val();
    var amount = $(order_seq_tr).find("[name='amount']").val();
    var amount_vat = $(order_seq_tr).find("[name='amount-vat']").val();
    var issue = $(order_seq_tr).find("[name='issue']").val();
    var comp_bank = $(order_seq_tr).find("[name='comp_bank']").val();
    var comp_account = $(order_seq_tr).find("[name='comp_account']").val();
    var comp_account_holder = $(order_seq_tr).find("[name='comp_account_holder']").val();

    var formData = {
        date: date,
        amount: amount,
        amount_vat: amount_vat,
        issue: issue,
        comp_bank: comp_bank,
        comp_account: comp_account,
        comp_account_holder: comp_account_holder,
        md_order_seq: orderseq
    };

    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: '/finance/insertOrderTransaction',
        data: JSON.stringify(formData),
        dataType: 'json',
        encode: true,
        success: function(response) {
            if (response.code == '200') {
                var comp_details = {
                        bank: comp_bank,
                        account: comp_account,
                        account_holder: comp_account_holder
                    }
                    //getTransactionsPerOrder(orderseq, comp_details);
                getTransactionListCount(searchKeyword, isUrgent);
            } else {
                alert('Invalid');
            }
        }
    });
}

function downloadPDF(pdf, biz_no, transaction_method, transaction_required_deadline) {
    const linkSource = `data:application/pdf;base64,${pdf}`;
    const downloadLink = document.createElement("a");
    const fileName = biz_no + "_" + transaction_method + "_" + transaction_required_deadline + ".pdf";
    downloadLink.href = linkSource;
    downloadLink.download = fileName;
    downloadLink.click();
}

function generatePDF(order_seq, comp_name, transaction_method, payment_date) {
    $('.loading').css('display', 'table');
    var generateList = [];
    generateList.push(order_seq)

    for (var i = 0; i < generateList.length; i++) {
        var obj = generateList[i];
        var seq_list = { "order_details": [{}] };
        seq_list.order_details = [];
        seq_list.order_details.push({ 'order_seq': obj });

        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/finance/generateOrderTransactionPDF',
            data: JSON.stringify(seq_list),
            responseType: 'blob',
            success: function(response) {
                if (response.code == '200') {
                    downloadPDF(response.message, comp_name, transaction_method, payment_date);
                    // 업데이트
                    $.ajax({
                        type: 'POST',
                        contentType: "application/json",
                        url: '/finance/updateSingleTransactionValidationDate?seq=' + order_seq,
                        responseType: "application/json",
                        success: function(response) {
                            if (response.code == '200') {
                                // success
                            } else if (response.code == '403') {
                                window.location.href = '/signIn';
                            } else {
                                alert(response.message);
                            }
                        }
                    });
                    reloadCurrentPage();
                } else if (response.code == '403') {
                    window.location.href = '/signIn';
                } else {
                    alert(response.message);
                }
                $('.loading').css('display', 'none');
            }
        });
    }
}

function updatePDFDate(orderseq) {
    var update_pdf = {
        md_order_seq: orderseq
    };
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/updateOrderPDF',
        data: JSON.stringify(update_pdf),
        dataType: "json",
        success: function(response) {
            if (response.code == "200") {} else {}
        }
    });
}

// function callPdfGeneratorAPI(pdf_payload, biz_no, transaction_method, payment_date) {
//     $('.loading').css('display', 'table');
//     $.ajax({
//         type: 'POST',
//         contentType: "application/json",
//         url: '/generateOrderTransactionPDF',
//         data: JSON.stringify(pdf_payload),
//         responseType: 'blob',
//         success: function (response) {
//             downloadPDF(response, biz_no, transaction_method, payment_date);
//             // 발급된 거 업데이트 하는 쿼리 필요
//             $('.loading').css('display', 'none');
//         }
//     });
// }

// function generateMultipleItemsPDF(pdf_payload, biz_no, transaction_method, payment_date) {
//     var timeout = 3500 + (1000 * pdf_payload.order_details.length);
//     callPdfGeneratorAPI(pdf_payload, biz_no, transaction_method, payment_date);
// }

function openPDFOnNewTab(base64EncodedPDF) {
    var byteCharacters = atob(base64EncodedPDF);
    var byteNumbers = new Array(byteCharacters.length);
    for (var i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    var byteArray = new Uint8Array(byteNumbers);
    var file = new Blob([byteArray], { type: 'application/pdf;base64' });
    var fileURL = URL.createObjectURL(file);
    window.open(fileURL);
}

// function callGroupedAPI(list) {
//     $.ajax({
//         type: 'POST',
//         contentType: "application/json",
//         url: '/groupOrderTransactionPayload',
//         data: JSON.stringify(list),
//         success: function (response) {
//             var objectPayload = response.object;
//             const result = Object.values(objectPayload).map((group) => ({
//                 order_details: group
//             }));
//             for (let i = 0; i < result.length; i++) {
//                 setTimeout(function() {
//                     generateMultipleItemsPDF(result[i], result[i].order_details[0].biz_no, result[i].order_details[0].transaction_method, result[i].order_details[0].payment_date);
//                 }, i * 1000);
//             }
//         }
//     });
// }

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

    getTransactionList(current, itemRange, searchKeyword, sortBy, orderBy, isUrgent);
    currentPage = current;

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

function getTransactionListCount(keyword, is_urgent) {
    $.ajax({
        url: '/finance/getOrderTransactionCount?keyword=' + keyword + '&is_urgent=' + is_urgent,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                count = Number(jsonObject);
                $("#totalOrders").text(count);
                totalPage = Math.ceil(count / itemRange);
                createPagination(currentPage, totalPage);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                count = 0;
                $("#totalOrders").text(count);
                totalPage = Math.ceil(count / itemRange);
                createPagination(currentPage, totalPage);
            }
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}

function callGenerateAllProcessingOrderPDF() {
    $('.loading').css('display', 'table');
    var _today = new Date();
    $.ajax({
        type: 'POST',
        url: '/finance/validateAllTransaction',
        responseType: 'application/json',
        success: function(response) {
            if (response.code == '200') {
                var zipFileName = response.message;
                url = "/finance/transactionFile?fileName=" + zipFileName
                window.open(url);
                window.close(url);

                $.ajax({
                    type: 'POST',
                    url: 'finance/updateAllIncompleteTransactionValidationDate?executeTime=' + _today.format('yyyy-MM-dd HH:mm:ss'),
                    responseType: 'application/json',
                    success: function(response) {
                        if (response.code == '200') {
                            reloadCurrentPage();
                        } else if (response.code == '403') {
                            window.location.href = '/signIn';
                        } else if (response.code == '404') {
                            alert("처리할 데이터가 없습니다.");
                        } else {
                            console.log(response.message);
                        }
                    },
                    error: function() {
                        $('.loading').css('display', 'none');
                    }
                });
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                alert(response.message);
            }
            $('.loading').css('display', 'none');
        },
        error: function() {
            console.log("An error has occurred.");
            $('.loading').css('display', 'none');
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


function callGenerateMultipleProcessingOrderPDF(list) {
    $('.loading').css('display', 'table');
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/finance/validateSelectedTransactionPDF',
        data: JSON.stringify(list),
        responseType: 'application/json',
        success: function(response) {
            if (response.code == '200') {
                var zipFileName = response.message;
                url = "/finance/transactionFile?fileName=" + zipFileName
                window.open(url);
                window.close(url);

                $.ajax({
                    type: 'POST',
                    url: '/finance/updateSelectedTransactionValidationDate',
                    contentType: "application/json",
                    responseType: 'application/json',
                    data: JSON.stringify(list),
                    success: function(response) {
                        if (response.code == '200') {
                            reloadCurrentPage();
                        } else if (response.code == '403') {
                            window.location.href = '/signIn';
                        } else {
                            console.log(response.message);
                        }

                    },
                    error: function() {
                        $('.loading').css('display', 'none');
                    }
                });

            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                alert(response.message);
            }
            $('.loading').css('display', 'none');
        },
        error: function() {
            console.log("An error has occurred.");
        }
    });
}

function callGenerateMultipleProcessingOrderExcel(list) {
    $('.loading').css('display', 'table');
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/finance/validateSelectedTransactionExcel',
        data: JSON.stringify(list),
        responseType: 'application/json',
        success: function(response) {
            if (response.code == '200') {
                var zipFileName = response.message;
                url = "/finance/transactionFile?fileName=" + zipFileName
                window.open(url);
                window.close(url);

            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                alert(response.message);
            }
            $('.loading').css('display', 'none');
        },
        error: function() {
            console.log("An error has occurred.");
        }
    });
}