$(document).ready(function () {
    
    var shippedTable = $('#shipped-table').DataTable({
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
    });

    var searchShippedTable = $('#search-shipped-table').DataTable({
        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
    });

    $('#search-shipped-order').click(function () {
        

        var trackingNo = $('#tracking-no').val();

        var content = {
            tracking_no: trackingNo
        };
        console.log(content);
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/searchShippedOrders',
            data: JSON.stringify(content),
            dataType: "json",
            success: function (response) {
                var shippedOrder = response.object;
                console.log(response);
                if(response.code == "200"){

                    $(".dataTables_empty").addClass('hide');
                    var tracking_no = shippedOrder.tracking_no;
                    var tracking_url = "<a href='http://www.dhl-gp.com/en/express/tracking.html?AWB="+tracking_no+"&brand=DHL'>" + tracking_no+"</a>";
                    var order_no = shippedOrder.order_no;
                    var order_url = "<a href='https://www.hanpoom.com/wp-admin/post.php?post="+order_no+"&action=edit'>" +order_no +"</a>";
                    var create_user = shippedOrder.create_user;
                    var create_datetime = shippedOrder.create_datetime;
                    var date;
                    var createTime = null;

                    if(shippedOrder.create_datetime){
                        date = new Date(shippedOrder.create_datetime);
                        createTime = date.getFullYear() + ". " + (date.getMonth()+1)  + ". "+ date.getDate() + " " + date.getHours() + ":" +date.getMinutes() + ":" + date.getSeconds();
                    }
                    
                    searchShippedTable.row.add([tracking_url, order_url, create_user, create_datetime]).draw(false);
                    
                }else if(response.code == "400"){
                    alert(response.Message);
                    
                }else{
                    alert("검색 실패");
                }

            }
        });
    });
    
    $(window).on("load",function(){
        $(".loader-wrapper").fadeOut("slow");
    });

});
