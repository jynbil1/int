var sortBy;
var orderBy;
var currentPage;
var itemRange;
var searchKeyword;
var transactionRow;
var totalPage;
var totalCount;
var orderStatus;

$(document).ready(function () {
    currentPage = $("#listPage").val();
    itemRange = $("#itemRange").val();
    searchKeyword = $(".search > input").val().trim();
    sortBy = $("#sortBy").val();
    orderBy = $("#orderBy").val();

    getOrderPackingSlipHistoryCount(searchKeyword);

    $("#valSeq, #orderRange, #createUser, #createTime, #order_cnt, #specialOrder").on("click", function () {
        var order = $(this).attr("data-order");
        var sort = $(this).attr("data-sort");

        if (order == 1) {
            $(this).attr("data-order", 0);
            getOrderPackingSlipHistory(currentPage, itemRange, searchKeyword, sort, 0);
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
            sortBy = sort;
            orderBy = order;
        } else {
            $(this).attr("data-order", 1);
            getOrderPackingSlipHistory(currentPage, itemRange, searchKeyword, sort, 1);
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
            sortBy = sort;
            orderBy = order;
        }
    });

    $("#searchBtn").on("click", function (event) {
        event.preventDefault();
        searchKeyword = $(".search > input").val().trim();
        currentPage = 1;
        $("#listPage").val(currentPage);
        $("#searchKeyword").val(searchKeyword);
        getOrderPackingSlipHistoryCount(searchKeyword);
    });

    $(".search > input").keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            currentPage = 1;
            $("#searchBtn").click();
        }
    });

    $('#item_range').change(function () {
        currentPage = 1;
        $("#listPage").val(currentPage);

        if ($(this).val() == "all") {
            itemRange = $("#totalProducts").text();
        } else {
            itemRange = $(this).val();
        }

        orderBy = $("#orderBy").val();
        $("#itemRange").val(itemRange);
        getOrderPackingSlipHistoryCount(searchKeyword);
    });

    $('#datePicker').on("change", function () {
        $(".search > input").val($(this).val());
    });
});

function getOrderPackingSlipHistory(page, item_range, keyword, sort, order) {
    $.ajax({
        url: '/operation/getOrderPackingSlipHistory?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            $.each(jsonObject, function (i, object) {
                var order_type = "";
                var document_type_btn = "";
                var label_url = object.label_url;
                switch (object.order_type) {
                    case "wc-moomoo":
                        order_type = "무무 배송";
                        break;
                    case "wc-together":
                        order_type = "합배송";
                        break;
                    default:
                        order_type = "기본 배송";
                        break;
                }

                switch (object.document_type) {
                    case "packing-slip":
                        document_type_btn = "<button type='button' class='send-notification primary regeneratePSPDFBtn' value='" + label_url + "'>주문서</button>";
                        break;
                    case "shipping-label":
                        document_type_btn = "<button type='button' class='send-notification primary regenerateShippingLabel' value='" + label_url + "'>운송장</button>";
                        break;
                    default:
                        document_type_btn = "<button type='button' class='send-notification primary regeneratePSPDFBtn' value='" + label_url + "'>주문서</button>";
                        break;
                }

                tr += '<tr id"' + object.val_seq + '">'
                    + '<td>' + object.val_seq + '</td>'
                    + '<td>' + object.order_range + '</td>'
                    + '<td>' + object.create_user + '</td>'
                    + '<td>' + object.create_time + '</td>'
                    + '<td>' + object.order_cnt + '</td>'
                    + '<td>' + order_type + '</td>'
                    + '<td>' + document_type_btn + '</td>'
                    + '</tr>';
            });
            $('.loading').css('display', 'none');
            $('tbody#operationOrderPackingList').html(tr);

            // $('.regeneratePSPDFBtn').click(function () {
            //     $('.loading').css('display', 'table');
            //     var selectedRow = $(this).parent().parent();
            //     var selectedValSeq = selectedRow.children().eq(0);

            //     $.ajax({
            //         url: '/operation/order-packing-slip/revalidatePSPDF?valSeq=' + selectedValSeq.text(),
            //         contentType: "application/json; charset=utf-8",
            //         type: "POST",
            //         dataType: "json",

            //         success: function (response) {
            //             if (response.code == "200") {
            //                 openPDFOnNewTab(response.message);
            //                 $('.loading').css('display', 'none');
            //             } else {
            //                 $('.loading').css('display', 'none');
            //                 alert.log(response.message);
            //             }
            //         }
            //     });
            // });

            $('.regenerateShippingLabel, .regeneratePSPDFBtn').click(function () {
                var label_url = $(this).val();

                if (label_url != null && label_url != "") {
                    printLabel(label_url);
                } else {
                    alert("No history available.")
                }
            });
        },
        error: function () {
            console.log("error");
        }
    });
}

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
    if ($("#item_range").val() == "all") {
        itemRange = $("#totalProducts").text();
    } else {
        itemRange = $("#item_range").val();
    }
    $("#itemRange").val(itemRange);
    
    getOrderPackingSlipHistory(current, itemRange, searchKeyword, sortBy, orderBy);

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

function getOrderPackingSlipHistoryCount(keyword) {
    $.ajax({
        url: '/operation/getOrderPackingSlipHistoryCount?keyword=' + keyword,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            count = Number(jsonObject);
            $("#totalProducts").text(count);
            totalPage = Math.ceil(count / itemRange);
            createPagination(currentPage, totalPage);
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}

function printLabel(fileURL) {
    $(".loading").css("display", "table");
    var posturl;

    if (fileURL == undefined) {
        posturl = '/operation/printOrderPackingLabel'
    } else {
        posturl = '/operation/printOrderPackingLabel?shipping_label_url=' + fileURL
    }

    $.ajax({
        type: 'POST',
        url: posturl,
        responseType: 'blob',
        success: function (response) {
            $(".progress-container").css("display", "none");
            $(".loading").css("display", "none");
            var byteCharacters = atob(response);
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var file = new Blob([byteArray], { type: 'application/pdf;base64' });
            var fileURL = URL.createObjectURL(file);
            var wnd = window.open(fileURL);
            wnd.print();
            //setTimeout(function () {
            //    wnd.close();
            //}, 4000);
        }
    })
}