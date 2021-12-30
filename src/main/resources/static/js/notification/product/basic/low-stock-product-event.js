var item_range, current_page, keyword, count, totalPage, sort, search_by, order;

$(document).ready(function() {
    item_range = $("#item_range").val();
    current_page = $("#listPage").val();
    keyword = $(".search > input").val();
    search_by = $("#selectRange").val();
    sort = $("#sortBy").val();
    order = $("#orderBy").val();

    getLowStockProductTotalCount(keyword, search_by);

    $("#searchBtn").on("click", function(event) {
        event.preventDefault();
        keyword = $(".search > input").val();
        current_page = 1;
        $("#listPage").val(current_page);
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage, sort, order);
        getLowStockProductTotalCount(keyword, search_by);
    });

    $('#item_range').change(function() {
        keyword = $(".search > input").val();
        current_page = 1;
        $("#listPage").val(current_page);
        item_range = $(this).val();
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage, sort, order);
        getLowStockProductTotalCount(keyword, search_by);
    });

    $("#prodID, #prodName, #whLoc, #whQty, #picLoc").on("click", function() {
        var order = $(this).attr("data-order");
        var sort = $(this).attr("data-sort");

        if (order == 1) {
            $(this).attr("data-order", 0);
            createPagination(current_page, totalPage, sort, order);
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
        } else {
            $(this).attr("data-order", 1);
            createPagination(current_page, totalPage, sort, order);
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
        }
    });
});

function createPagination(current, total, sort, order) {
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

    getLowStockProductList(current, item_range, search_by, keyword, sort, order);

    var pageTable = "<ul class='page-list'>";
    pageTable += "<li onclick='createPagination(" + '"1","' + totalPage + '","' + sort + '","' + order + '"' + ");'>&lt;&lt;</li>";

    for (var pageCell of rangeWithDots) {
        if (pageCell != '...') {
            pageTable += '<li onclick="createPagination(' + "'" + pageCell + "','" + totalPage + "','" + sort + "','" + order + "'" + ');">' + pageCell + '</li>';
        } else {
            pageTable += '<li>' + pageCell + '</li>';
        }
    }

    pageTable += "</li><li onclick='createPagination(" + '"' + totalPage + '","' + totalPage + '","' + sort + '","' + order + '"' + ");'>&gt;&gt;</li>";
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

function getLowStockProductList(page, item_range, search_by, keyword, sort, order) {
    $.ajax({
        url: '/getLowStockProductList?page=' + page + '&item_range=' + item_range + '&search_by=' + search_by + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            const jsonObject = response.object;
            var tr = '';
            $.each(jsonObject, function(i, object) {
                tr += '<tr id="' + object.product_id + '">' +
                    '<td class="product-id" data-label="상품 ID">' + object.product_id + '</td>' +
                    '<td class="product-name" data-label="상품 명"><a href="/md-edit-product?product_id=' + object.product_id + '">' + object.product_name + '</a></td>' +
                    '<td class="product-barcode" data-label="바코드">' + (object.barcode == null ? "N/A" : object.barcode) + '</td>' +
                    '<td class="wh-location" data-label="보관동 위치">' + object.wh_in_location + '</td>' +
                    '<td class="wh-expdate" data-label="유통기한">' + object.wh_in_expdate + '</td>' +
                    '<td class="qty" data-label="보관동 수량">' + object.wh_in_qty + '</td>' +
                    '<td class="rate" data-label="운영동 상품 적재율 (%)"><select id="' + object.product_id + '"><option value="--">--</option><option value="5">5</option><option value="10">10</option><option value="15">15</option><option value="20">20</option><option value="25">25</option><option value="30">30</option><option value="35">35</option><option value="40">40</option><option value="45">45</option><option value="50">50</option><option value="55">55</option><option value="60">60</option><option value="65">65</option><option value="70">70</option><option value="75">75</option><option value="80">80</option><option value="85">85</option><option value="90">90</option><option value="95">95</option><option value="100">100</option></select></td>' +
                    '<td class="pick-location" data-label="운영동 위치">' + (object.pick_location == null ? "N/A" : object.pick_location) + '</td>' +
                    '<td class="submit-btn"><button class="send-notification primary" data-id="' + object.product_id + '"> 요청 </button>' +
                    '</td></tr>';
            });
            $('#lowStockList').html(tr);
            $('.send-notification').on("click", function() {
                var product_id = $(this).attr("data-id");
                var product_load_rate = $(this).parent().parent().find('select option:selected').val();
                var loc = $(this).parent().parent().find('.wh-location').html();
                var product_name = $(this).parent().parent().find('.product-name').html();
                var barcode = $(this).parent().parent().find('.product-barcode').html();

                var lowStockProductContent = {
                    product_id: product_id,
                    product_load_rate: product_load_rate,
                    loc: loc,
                    product_name: product_name,
                    barcode: barcode
                };
                console.log(lowStockProductContent)

                $.ajax({
                    url: '/low-stock-product/notify',
                    type: 'POST',
                    data: JSON.stringify(lowStockProductContent),
                    dataType: 'json',
                    contentType: 'application/json',
                    encode: true,
                    success: function(response) {
                        if (response.code === '200') {
                            $(".notification-msg").removeClass("warning").addClass("success");
                            $(".notification-msg").html("<i class='fas fa-check-circle'></i>Stock request notification for Product ID " + product_id + " has been sent!");
                            $(".notification-msg-container").fadeIn(500, function() {
                                $(".notification-msg-container").fadeOut(3000);
                            });
                        } else if (response.code === '400') {
                            $(".notification-msg").removeClass("success").addClass("warning");
                            $(".notification-msg").html("<i class='fas fa-exclamation-circle'></i>An error has occurred.");
                            $(".notification-msg-container").fadeIn(500, function() {
                                $(".notification-msg-container").fadeOut(3000);
                            });
                        } else {
                            $(".notification-msg").removeClass("success").addClass("warning");
                            $(".notification-msg").html("<i class='fas fa-exclamation-circle'></i>An error has occurred.");
                            $(".notification-msg-container").fadeIn(500, function() {
                                $(".notification-msg-container").fadeOut(3000);
                            });
                        }
                    }
                });
            });
        }
    });
}

function getLowStockProductTotalCount(keyword, search_by) {
    $.ajax({
        url: '/getLowStockProductTotalCount?keyword=' + keyword + '&search_by=' + search_by,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            const jsonObject = response.object;
            $.each(jsonObject, function(i, object) {
                count = object.total_count;
                $("#totalCounts").text(count);
                totalPage = Math.ceil(count / item_range);
                createPagination(current_page, totalPage, sort, order);
            });
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}