let orderSeq;
let processDate;
let productName;
let searchParams = new URLSearchParams(window.location.search);
let errorInquiry = `아래 항목을 확인 해 주세요.
</p>
<ol>
    <li>발주 요청 이메일(발주서)에 <strong>첨부된 링크를 클릭</strong>하셔서 들어오신건가요?</li>
    <li>발주서 발송 시점 기준 <strong>2주가 지나</strong>지 않았나요?</li>
</ol>
<p class="message-box">
    추가 문의가 있으시거나, 페이지가 정상작동 하지 않을 시에는 아래 담당자에게 문의주세요.<br><br>
    <strong>유나이티드보더스(주) 유연수 매니저<strong><br> 

    <span class="email">
        Contact: <a href="tel:010-3794-7880">010-3794-7880</a><br>
        E-mail: <a href="mailto:md@hanpoom.com">md@hanpoom.com</a>
    </span>`;

$(document).ready(function() {
    console.log("123")
    orderSeq = searchParams.get("order_seq");
    cert_1 = searchParams.get("certIDFirst");
    cert_2 = searchParams.get("certIDSecond");
    validateBill(orderSeq, cert_1, cert_2);
});

function validateBill(orderSeq, cert_1, cert_2) {
    let result;
    let leftContent;
    data = {
        order_seq: orderSeq,
        certIDFirst: cert_1,
        certIDSecond: cert_2
    }
    console.log(data);
    $.ajax({
        url: '/mfc/shipment/confirmBillValidation',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const order = response.object;
                console.log(response);
                $('.message').addClass('success');
                result = order.message + `<br><br>
                    발주 번호: <span id="orderSeq">
                        <a style="text-decoration: none; color: #ff3b2c;
                        "href="/mfc/shipment/record-shipping-label?order_seq=` + order.order_seq +
                    `&certIDFirst=` + cert_1 + `&certIDSecond=` + cert_2 + `">` +
                    order.order_seq + `</a></span><br>
                처리 일자: <span id = "orderDate"> ` + (order.update_dtime == null ? '' : new Date(order.update_dtime).format('yyyy년 MM월 dd일 HH:mm')) + `</span><br>
                품목: <span id = "productName"> ` + order.product_summary + ` </span>`;

                $('.message-box').html(result);

                leftContent = `<i class="fa fa-check-circle"  aria-hidden="true"></i>
                <h3 id="status">처리 성공</h3>`;

            } else if (response.code == '403') {
                $('.message').addClass('error');
                $('.message-box').html(errorInquiry);
                $('#status').text('유효하지 않은 링크');
                leftContent = `<i class="fa fa-times-circle"  aria-hidden="true"></i>
                <h3 id="status">링크가 유효하지 않음</h3>`;

            } else {
                $('.message').addClass('error');
                $('.message-box').html(errorInquiry);
                $('#status').text('처리 불가');
                leftContent = `<i class="fa fa-times-circle"  aria-hidden="true"></i>
                <h3 id="status">오류 발생</h3>`;
            }

            $('.left-side').html(leftContent);
            $('.right-side').show();
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