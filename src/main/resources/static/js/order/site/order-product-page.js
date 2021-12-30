var sortBy;
var orderBy;
var currentPage;
var itemRange;
var searchKeyword;
var banjjakOrder;

$(document).ready(function () {
    currentPage = $("#listPage").val();
    itemRange = $("#itemRange").val();
    searchKeyword = $(".search > input").val();
    sortBy = $("#sortBy").val();
    orderBy = $("#orderBy").val();
    banjjakOrder = $('#showSpecialOrder').is(":checked") ? 1 : 0;

    getSiteOrderListCount(searchKeyword, banjjakOrder);

    $("#showSpecialOrder").on("click", function () {
        banjjakOrder = $('#showSpecialOrder').is(":checked") ? 1 : 0;
        getSiteOrderListCount(searchKeyword, banjjakOrder);
    });

    $(".search").change(function () {
        orderDate = $('#order-date-filter').val();
        getSiteOrderListCount(searchKeyword, banjjakOrder);
    });

    $(".search > button").on("click", function (event) {
        event.preventDefault();
        searchKeyword = $(".search > input").val();
        currentPage = 1;
        $("#listPage").val(currentPage);
        $("#searchKeyword").val(searchKeyword);
        getSiteOrderListCount(searchKeyword, banjjakOrder);
    });

    $(".search > input").keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            currentPage = 1;
            $("#listPage").val(currentPage);
            searchKeyword = $(this).val().trim();
            $("#searchKeyword").val(searchKeyword);
            getSiteOrderListCount(searchKeyword, banjjakOrder);
        }
    });

    $("#item_range").change(function () {
        currentPage = 1;
        $("#listPage").val(currentPage);
        itemRange = $(this).val();
        orderBy = $("#orderBy").val();
        $("#itemRange").val(itemRange);
        getSiteOrderListCount(searchKeyword, banjjakOrder);
    });

    $("tbody#orderProductList").on("click", "tr", function () {
        $(this).toggleClass("selected");
    });

    $("tbody#orderProductList").on("keyup", "input[type='text']", function () {
        var $tr = $(this).closest('tr');
        var carton_unit = Number($tr.find('input[name="carton-unit"').val());
        var carton_quantity = Number($tr.find('input[name="carton-qty"').val());
        var orderQty = carton_unit * carton_quantity;
        var total_quantity = Number(orderQty);
        var supply_price = parseFloat($tr.find('input[name="unit-price"').val());
        var shipping_fee = Number($tr.find('input[name="shipping-fee"').val());
        var order_price = total_quantity * supply_price + shipping_fee;

        $tr.find('input[name="order-qty"').val(orderQty);

        if (carton_quantity === 0) {
            order_price = 0;
        }

        $tr.find('input[name="order-price"').val(order_price);
        $tr.find('input[name="order-price"').css("border", "1px solid #E0E0E0").css("background", "#FFF");
    });

    $('#submitOrder').on('click', function (event) {
        if ($(".selected").length > 0) {
            $('.loading').css('display', 'table');
            var list = [];
            $(".selected").each(function () {
                var order_date = $(this).find("input[name='order-date']").val();
                var company_id = $(this).find("[name='company-id']").text();
                var company_name = $(this).find("[name='company-name']").text();
                var product_id = $(this).find("[name='product-id']").text();
                var product_name = $(this).find("[name='product-name']").text();
                var order_qty = $(this).find("[name='order-qty']").val();
                var unit_price = $(this).find("[name='unit-price']").val();
                var shipping_fee = $(this).find("[name='shipping-fee']").val();
                var order_price = $(this).find("[name='order-price']").val();
                var evid_type = $(this).find("[name='evid-type']").val();
                var is_taxable = $(this).find("[name='is-taxable']").text();
                var transaction_req_date_type = $(this).find("[name='transaction-req-date-type']").text();
                var content = {
                    order_date: order_date,
                    company_id: company_id,
                    company_name: company_name,
                    product_id: product_id,
                    product_name: product_name,
                    order_qty: order_qty,
                    unit_price: unit_price,
                    shipping_fee: shipping_fee,
                    order_price: order_price,
                    evid_type: evid_type,
                    is_taxable: is_taxable,
                    transaction_req_date_type: transaction_req_date_type
                };

                if (isNaN(order_price) == true || order_price == "") {
                    $('.loading').css('display', 'none');
                    $(this).find("[name='order-price']").css("border", "1px solid #ff3b2c").css("background", "#FFBABA");
                } else {
                    list.push(content);
                }
            });
            
            if (list.length !== 0) {
                $.ajax({
                    type: 'POST',
                    contentType: "application/json",
                    url: '/siteOrderProduct',
                    data: JSON.stringify(list),
                    dataType: "json",
                    success: function (response) {
                        $('.loading').css('display', 'none');
                        if (response.code == "200") {
                            $("tbody#orderProductList tr").removeClass('selected');
                            $(".notification-msg").html('<i class="fas fa-check-circle"></i>작업 성공적으로 완료되었습니다.').removeClass("warning").addClass("success");
                            $(".notification-message").animate({ opacity: 1 }, 500, function () {
                                $('.notification-message').css('visibility', 'visible');
                                $(".notification-message").animate({ opacity: 0 }, 5000, function () {
                                    $('.notification-message').css('visibility', 'hidden');
                                });
                            });
                        } else if (response.code == "500") {
                            $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>' + response.message).removeClass("success").addClass("warning");
                            $(".notification-message").animate({ opacity: 1 }, 500, function () {
                                $('.notification-message').css('visibility', 'visible');
                                $(".notification-message").animate({ opacity: 0 }, 5000, function () {
                                    $('.notification-message').css('visibility', 'hidden');
                                });
                            });
                        }
                        else {
                            $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>발주 실패 (세션확인 후 그래도 실패하면 관리자에게 문의해주세요)').removeClass("success").addClass("warning");
                            $(".notification-message").animate({ opacity: 1 }, 500, function () {
                                $('.notification-message').css('visibility', 'visible');
                                $(".notification-message").animate({ opacity: 0 }, 5000, function () {
                                    $('.notification-message').css('visibility', 'hidden');
                                });
                            });
                        }
                    },
                    error: function () {
                        $('.loading').css('display', 'none');
                        $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>An error has occurred, please try again.').removeClass("success").addClass("warning");
                        $(".notification-message").animate({ opacity: 1 }, 500, function () {
                            $('.notification-message').css('visibility', 'visible');
                            $(".notification-message").animate({ opacity: 0 }, 5000, function () {
                                $('.notification-message').css('visibility', 'hidden');
                            });
                        });
                    }
                });
            }
        } else {
            $(".notification-msg").html('<i class="fas fa-exclamation-circle"></i>선택된 줄이 없습니다. 발주할 줄을 선택해주세요.').removeClass("success").addClass("warning");
            $(".notification-message").animate({ opacity: 1 }, 500, function () {
                $('.notification-message').css('visibility', 'visible');
                $(".notification-message").animate({ opacity: 0 }, 5000, function () {
                    $('.notification-message').css('visibility', 'hidden');
                });
            });
        }
    });
});

function getSiteOrderList(page, item_range, keyword, sort, order, banjjak_order) {
    $('.loading').css('display', 'table');
    $.ajax({
        url: '/siteOrderProductList?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order + '&banjjak_order=' + banjjak_order,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                var tr = '';
                $.each(jsonObject, function (i, object) {
                    var evid_type = object.is_taxable;
                    var evid_container = '';

                    if (evid_type == 1) {
                        evid_container = '<option value = "0" selected>세금계산서</option><option value = "3">예치금(과세)</option><option value = "4">법인카드</option>';
                    } else {
                        evid_container = '<option value = "1" selected>계산서</option><option value = "2">예치금(면세)</option><option value = "4">법인카드</option>';
                    }

                    tr += '<tr>' +
                        '<td name="company-id" style="display: none;">' + object.company_id + '</td>' +
                        '<td name="company-name">' + object.company_name + '</td>' +
                        '<td name="product-id" style="display: none;">' + object.product_id + '</td>' +
                        '<td name="product-name">' + object.product_name + '</td>' +
                        '<td name="stock">' + object.stock + '</td>' +
                        '<td name="order-date"><input type="date" name="order-date" value="' + object.order_date + '"></td>' +
                        '<td><input type="text" placeholder="카톤단위" name="carton-unit" value="' + object.carton_qty + '" style="width: 50px;" readonly></td>' +
                        '<td><input type="text" placeholder="카톤수량" name="carton-qty" style="width: 70px;"></td>' +
                        '<td><input type="text" name="order-qty" style="width: 50px;" readonly/></td>' +
                        '<td><input type="text" placeholder="공급가" name="unit-price" value="' + object.unit_price + '" style="width: 90px;"></td>' +
                        '<td><input type="text" placeholder="배송비" name="shipping-fee" value="' + object.shipping_fee + '" style="width: 90px;"></td>' +
                        '<td><input type="text" name="order-price" style="width: 90px;" readonly/></td>' +
                        '<td><select name="evid-type">' + evid_container + '</select></td>' +
                        '<td name="is-taxable" style="display: none;">' + object.is_taxable + '</td>' +
                        '<td name="transaction-req-date-type" style="display: none;">' + object.transaction_req_date_type + '</td>' +
                        '</tr>';
                });

                $('tbody#orderProductList').html(tr);

            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                var tr = '<tr class="result-message">\
                            <td colspan="19">\
                                검색 결과가 없습니다.\
                            </td>\
                        </tr>';
                $('tbody#orderProductList').html(tr);
            } else {
                console.log(response.message);
            }
            $('.loading').css('display', 'none');
        }
    });
}

function getSiteOrderListCount(keyword, banjjak_order) {
    $.ajax({
        url: '/getSiteOrderProductCount?keyword=' + keyword + '&banjjak_order=' + banjjak_order,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                count = Number(jsonObject);
                $("#totalOrders").text(count);
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
            }
        },
        async: true,
        error: function () {
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
    getSiteOrderList(currentPage, itemRange, searchKeyword, sortBy, orderBy, banjjakOrder);
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