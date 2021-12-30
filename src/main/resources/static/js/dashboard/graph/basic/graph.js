var dateInterval;


$(document).ready(function() {
    dateInterval = $("#dateInterval").val();
    $('#dateInterval').change(function () {
        dateInterval = $(this).val();
        getOrderRecordData(dateInterval);
    });

    getOrderRecordData(dateInterval);
    getInTransitOrders();
    getOOSbyProductGrade();
});

function getInTransitOrders(){
    
    $.ajax({
        url: '/dashboard/getInTransitOrders',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        encode: true,
        success: function(response) {
            if (response.code == "200") {
                console.log(response.object)
                $('#in-transit-orders').html(response.object);
                
            } else if (response.code == "403") {
                window.location.href = '/signIn';
            }
        }
    });
};

function getOOSbyProductGrade(){
    
    $.ajax({
        url: '/dashboard/getOOSbyProductGrade',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        encode: true,
        success: function(response) {
            if (response.code == "200") {
                console.log(response.object);
                var tr = null;
                $.each(response.object, function (inx, obj) {
                    tr += '<tr><td>' + obj.product_grade + '</td><td>' + obj.oos_rate + '</td></tr>'
                });

                $('#OOSGradeTable tbody').html(tr);
            } else if (response.code == "403") {
                window.location.href = '/signIn';
            }
        }
    });
};

function getOrderRecordData(dateInterval){
    $.ajax({
        url: '/dashboard/getOrderRecord?dateInterval=' + dateInterval,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        encode: true,
        success: function(response) {
            if (response.code == "200") {
                console.log(response.object);

                var chartLabels = [];
                var chartDataOrder = [];
                var chartDataSKU = [];
                var chartDataCost = [];
                var chartDataCostOrig = [];

                $.each(response.object, function (inx, obj) {
                    chartLabels.push(obj.order_date);
                    chartDataSKU.push(obj.order_sku);
                    chartDataOrder.push(obj.order_cnt);
                        cost = obj.order_cost;
                        costFloat = parseFloat(cost.replace(/,/g, ''));
                        dataCost = Math.ceil(costFloat / 200000);
                        console.log('datacost', dataCost);
                    chartDataCost.push(dataCost);
                    chartDataCostOrig.push(obj.order_cost);
                });

                console.log(chartDataSKU);
                console.log(chartDataOrder);
                console.log(chartDataCost);
                console.log(chartDataCostOrig);

                var barChartData = {
                    labels: chartLabels,
                    datasets: [
                        {
                            label: "발주 상품 SKU",
                            data: chartDataSKU,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(255, 99, 132, 0.2)'
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(255, 99, 132, 1)'
                            ],
                            borderWidth: 1
                        },
                        {
                            label: "발주 건",
                            data: chartDataOrder,
                            backgroundColor: [
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(54, 162, 235, 0.2)'

                            ],
                            borderColor: [
                                'rgba(54, 162, 235, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(54, 162, 235, 1)'
                            ],
                            borderWidth: 1
                        },
                        {
                            label: "발주 금액 (*200,000)",
                            data: chartDataCost,
                            backgroundColor: [
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 206, 86, 0.2)'

                            ],
                            borderColor: [
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 206, 86, 1)'
                            ],
                            borderWidth: 1
                        }
                    ],
                    options: {
                        responsive: true,
                        maintainAspectRatio: false
                    }
                }

                createChart(barChartData);
            } else if (response.code == "403") {
                window.location.href = '/signIn';
            }
        }
    });
};


function createChart(barChartData) { 
    document.getElementById("chart-container").innerHTML = '&nbsp;';
    document.getElementById("chart-container").innerHTML = '<canvas id="canvas"></canvas>';
    var ctx = document.getElementById("canvas").getContext("2d");

    BarChart = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
        options: {
            scales: {
                x: {
                    stacked: true
                },
                y: {
                    stacked: true
                }
            }
        }
    });
};



