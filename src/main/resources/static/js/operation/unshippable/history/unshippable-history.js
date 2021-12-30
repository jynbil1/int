var redirect_;
var summary_details;
var isValidUser;

$(document).ready(function () {
    var search_complete_flag_value = $("#search-complete-flag-value").val();
    console.log(search_complete_flag_value)
    if(search_complete_flag_value == "on"){
        $('input:checkbox[id="search-complete-flag"]').attr('checked',true);
    }

    var search_md_rep_type_value = $("#search-md-rep-type-value").val();
    console.log(search_md_rep_type_value)
    if(search_md_rep_type_value == "on"){
        $('input:checkbox[id="search-md-rep-type"]').attr('checked',true);
    }

    var search_cx_flag_value = $("#search-cx-flag-value").val();
    console.log(search_cx_flag_value)
    if(search_cx_flag_value == "on"){
        $('input:checkbox[id="search-cx-flag"]').attr('checked',true);
    }

    $("#unshippableHistory>tr").each(function () {
        var self = $(this);
        var access_cx = self.find("[name='access-cx']").val();
        var access_md = self.find("[name='access-md']").val();
        var access_lo = self.find("[name='access-lo']").val();
    
        var cx_flag_value = self.find("#cx-flag").val();
        // console.log("cx-flag : " + cx_flag_value)
        self.find('select[name=cx-flag] option').removeAttr('selected');
        self.find('select[name=cx-flag] option[value=' + cx_flag_value + ']').attr('selected','selected');

        var customer_flag_value = self.find("#customer-flag").val();
        // console.log("customer-flag : " + customer_flag_value)
        self.find('select[name=customer-flag] option').removeAttr('selected');
        self.find('select[name=customer-flag] option[value=' + customer_flag_value + ']').attr('selected','selected');
        
        var md_rep_type_value = self.find("#md-rep-type").val();
        // console.log("md_rep_type : " + md_rep_type_value)
        self.find('select[name=md-rep-type] option').removeAttr('selected');
        self.find('select[name=md-rep-type] option[value=' + md_rep_type_value + ']').attr('selected',true);

        var md_order_date_value = self.find("input[name='md-order-date']").val();
        if(typeof md_order_date_value != "undefined" && md_order_date_value != "" && md_order_date_value != null){
            self.find('select[name=md-rep-type] option').removeAttr('selected');
            self.find('select[name=md-rep-type] option[value="1"]').attr('selected','selected')
        }
        var complete_value = self.find("[name='complete-val']").val();
        
        if(access_cx == 1){
            self.find('select[name=cx-flag]').removeAttr('disabled');
            self.find('select[name=customer-flag]').removeAttr('disabled');
            self.find('input[name=customer-email]').removeAttr('disabled');
            self.find('.send-email-btn').removeAttr('disabled');
            self.find('.save-btn').removeAttr('disabled');
        }
        if(access_md == 1){
            self.find('select[name=md-rep-type]').removeAttr('disabled');
            self.find('input[name=md-order-date]').removeAttr('disabled');
            self.find('input[name=expected-enter-date]').removeAttr('disabled');
            self.find('input[name=md-remark]').removeAttr('disabled');
            self.find('.save-btn').removeAttr('disabled');
        }
        if(access_lo == 1){
            self.find('input[name=complete]').removeAttr('disabled');
            self.find('input[name=cancel]').removeAttr('disabled');
        }

        //row color
        if(complete_value == 1){
            self.find('[name=complete]').attr('checked',true);
            self.addClass('row-gray')
        }else if(complete_value == 2){
            self.find('[name=cancel]').attr('checked',true);
            self.find('[name=cancel]').attr('disabled',true);
            self.addClass('row-gray')
        }else{
            self.find('[name=complete]').removeAttr('checked');
            self.find('[name=cancel]').removeAttr('checked');
            self.removeClass('row-gray')
        }
        console.log(cx_flag_value);
        if(cx_flag_value == 1){
            self.find('.send-email-btn').attr('disabled', true);
            self.addClass('row-blue')
        }else{
            self.removeClass('row-blue')
        }
    });

    $(".send-email-btn").on( "click", function() {
        var list=[];
        var order_no = $(this).attr("data-id");
        var flag = true;
        $("#unshippableHistory>tr").each(function () {

            var self = $(this);
            var other_seq = self.find("[name='seq']").val();
            var other_cx_flag = self.find("[name='cx-flag']").val();
            var other_customer_flag = self.find("[name='customer-flag']").val();
            var other_order_no = self.find("[name='order-no']").val();
            var other_product_name = self.find("[name='product-name']").val();
            var other_order_qty = self.find("[name='order-qty']").val();
            var other_need_qty = self.find("[name='need-qty']").val();
            var other_md_rep_type = self.find("[name='md-rep-type']").val();
            var other_md_order_date = self.find("[name='md-order-date']").val();
            var other_expected_enter_date = self.find("[name='expected-enter-date']").val();
            var other_md_remark = self.find("[name='md-remark']").val();
            var other_customer_email = self.find("[name='customer-email']").val();

            var formData = {
                  seq : other_seq
                , cx_flag : 1
                , customer_flag : other_customer_flag
                , order_no : other_order_no
                , product_name : other_product_name
                , order_qty : other_order_qty
                , need_qty : other_need_qty
                , md_rep_type : other_md_rep_type
                , md_order_date : other_md_order_date
                , expected_enter_date : other_expected_enter_date
                , md_remark : other_md_remark    
                , customer_email : other_customer_email
            }

            if(order_no == other_order_no){
                if(other_md_rep_type == "0"){
                    alert("md팀이 확인하지않는 상품(" + order_no+ "-" +other_product_name + ")이 있어서 메일 발송이 불가합니다.")
                    $(this).removeClass("row-green");
                    $(this).removeClass("row-white");
                    $(this).addClass("row-red");
                    flag = false;
                }
                list.push(formData)
            }
        });

        console.log(list)
        if(!flag){
            return false;
        }
        $.ajax({
            url: '/operation/sendemail',
            type:'POST',
            data: JSON.stringify(list),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success:function(response){
                
                if (response.code == '200') {
                    alert("성공");
                    var product = response.object;
                    var order_no = product.order_no;

                    $("#unshippableHistory>tr").each(function () {
                        var self = $(this);
                        var other_order_no = self.find("[name='order-no']").val();

                        if(order_no == other_order_no){
                            self.find('select[name=cx-flag] option').removeAttr('selected');
                            self.find('select[name=cx-flag] option[value= "1"]').attr('selected','selected');

                            $(this).removeClass("row-red");
                            $(this).removeClass("row-white");
                            $(this).addClass("row-green");
                        }
                    });

                } else if (response.code == '403'){
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                }else if (response.code == '500'){
                    alert("서버 에러 : " + response.message)
                }
            }
        });// end getOneTransaction

    }); //end edit btn'

    $(".save-btn").on( "click", function() {
        let normalList=[];
        let waitingList=[];
        let partialRefundList = [];
        let order_no = $(this).attr("data-id");
        var flag = true;
        let all_refund_flag = 0;
        let change_customer_flag = 0;
        let length =0;
        let count = 0;
        var wh_unshippable_seq;
        $("#unshippableHistory>tr").each(function () {
            
            var self = $(this);
            var other_seq = self.find("[name='seq']").val();
            var other_cx_flag = self.find("[name='cx-flag']").val();
            var other_customer_flag = self.find("[name='customer-flag']").val();
            var other_customer_flag_ori = self.find("#customer-flag").val();
            var other_order_no = self.find("[name='order-no']").val();
            var other_product_id = self.find("[name='md-product-id']").val();
            var other_product_name = self.find("[name='product-name']").val();
            var other_order_qty = self.find("[name='order-qty']").val();
            var other_need_qty = self.find("[name='need-qty']").val();
            var other_md_rep_type = self.find("[name='md-rep-type']").val();
            var other_md_order_date = self.find("[name='md-order-date']").val();
            var other_expected_enter_date = self.find("[name='expected-enter-date']").val();
            var other_md_remark = self.find("[name='md-remark']").val();
            var other_customer_email = self.find("[name='customer-email']").val();
            var chkcomplete = self.find("[name='complete']").is(":checked");
            var chkcancel = self.find("[name='cancel']").is(":checked");
            wh_unshippable_seq = self.find("[name='wh-unshippable-seq']").val();

            var formData = {
                  seq : other_seq
                , cx_flag : other_cx_flag
                , customer_flag : other_customer_flag
                , customer_flag_ori : other_customer_flag_ori
                , order_no : other_order_no
                , md_product_id : other_product_id
                , product_name : other_product_name
                , order_qty : other_order_qty
                , need_qty : other_need_qty
                , md_rep_type : other_md_rep_type
                , md_order_date : other_md_order_date
                , expected_enter_date : other_expected_enter_date
                , md_remark : other_md_remark    
                , customer_email : other_customer_email
                , wh_unshippable_seq : wh_unshippable_seq
            }
            
            if(order_no == other_order_no ){
                if(chkcomplete == false && chkcancel == false){
                    if(other_customer_flag == "3"){
                        all_refund_flag = 1
                        partialRefundList.push(formData)
                    }else if(other_customer_flag == "2"){
                        if(other_md_rep_type == "5"){
                            count++
                        }
                        if(other_customer_flag_ori != "2"){
                            change_customer_flag = 1
                        }
                        waitingList.push(formData)
                    }else if(other_customer_flag == "1"){
                        count++
                        partialRefundList.push(formData)
                    }else{
                        normalList.push(formData)
                    }
                }else{ 
                    console.log(formData)
                    console.log(chkcomplete)
                    console.log(chkcancel)
                    flag=false;
                }
            }

        });
        
        let allRefundList = [];

        length = normalList.length + partialRefundList.length + waitingList.length + allRefundList.length;

        if(all_refund_flag == 1){
            allRefundList = Object.assign(partialRefundList, waitingList);
            partialRefundList=[];
            waitingList=[];
            count = length;
        }
        console.log(count)
        console.log(length)
        let is_complete_slack = count == length ? 1:0;

        let responseData = {

            partialRefundList : partialRefundList
          , waitingList : waitingList
          , allRefundList : allRefundList
          , normalList : normalList
          , length : length
          , is_complete_slack : is_complete_slack
          , update_wait_flag : change_customer_flag
          , wh_unshippable_seq : wh_unshippable_seq
          
        }

        if(!flag){
            alert("미출고 처리가 완료된 상품은 update가 되지않습니다.")
            return false
        }
        console.log(responseData)
        $.ajax({
            url: '/operation/updateUnshippableDetail',
            type:'POST',
            data: JSON.stringify(responseData),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success:function(response){
                
                if (response.code == '200') {
                    alert("성공");

                    var product = response.object;
                    var order_no = product.order_no;
                    var customer_flag = product.customer_flag;
                    console.log(response)
                    $("#unshippableHistory>tr").each(function () {
                        var self = $(this);
                        var other_order_no = self.find("[name='order-no']").val();

                        if(order_no == other_order_no ){
                            if(customer_flag == 3 ){
                                self.find('select[name=customer-flag] option').removeAttr('selected');
                                self.find('select[name=customer-flag] option[value= "3"]').attr('selected','selected');

                                $(this).removeClass("row-red");
                                $(this).removeClass("row-white");
                                $(this).addClass("row-green");
                            }

                            
                        }
                    });
                } else if (response.code == '403'){
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                }else if (response.code == '500'){
                    alert("서버 에러 : " + response.message)
                }
            }
        });// end getOneTransaction

    }); //end edit btn'

    $(".complete-btn").on( "click", function() {
        var self = $(this).parent().parent();
        var wh_unshippable_seq = self.find("[name='wh-unshippable-seq']").val();
        var order_no = self.find("[name='order-no']").val();
        var chkcomplete = self.find("[name='complete']").is(":checked")?1:0;
        
        var formData = {
              seq : wh_unshippable_seq
            , complete : chkcomplete
        }
        
        // if(complete.prop("checked")){
        //     alert("미출고 처리가 완료된 상품입니다.")
        //     return false;
        // }

        console.log(formData)

        $.ajax({
            url: '/operation/updateUnshippable',
            type:'POST',
            data: JSON.stringify(formData),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success:function(response){
                
                if (response.code == '200') {
                    alert("성공");
                    $("#unshippableHistory>tr").each(function () {
                        var self = $(this);
                        var other_order_no = self.find("[name='order-no']").val();

                        if(order_no == other_order_no){
                            console.log(chkcomplete);
                            if(chkcomplete == true){
                                self.find('[name=complete]').attr('checked',true);
                            }else{
                                self.find('[name=complete]').removeAttr('checked');
                            }
                            
                            $(this).removeClass("row-red");
                            $(this).removeClass("row-white");
                            $(this).addClass("row-green");
                        }
                    });
                } else if (response.code == '403'){
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                }else if (response.code == '500'){
                    alert("서버 에러 : " + response.message)
                }
            }
        });// end getOneTransaction

    }); //end edit btn'

    $(".cancel-btn").on( "click", function() {
        var self = $(this).parent().parent();
        var wh_unshippable_seq = self.find("[name='wh-unshippable-seq']").val();
        var flag = true;
        let normalList=[];
        $("#unshippableHistory>tr").each(function () {
            
            var self = $(this);
            var other_seq = self.find("[name='seq']").val();
            var other_cx_flag = self.find("[name='cx-flag']").val();
            var other_customer_flag = self.find("[name='customer-flag']").val();
            var other_customer_flag_ori = self.find("#customer-flag").val();
            var other_order_no = self.find("[name='order-no']").val();
            var other_product_id = self.find("[name='md-product-id']").val();
            var other_product_name = self.find("[name='product-name']").val();
            var other_order_qty = self.find("[name='order-qty']").val();
            var other_need_qty = self.find("[name='need-qty']").val();
            var other_md_rep_type = self.find("[name='md-rep-type']").val();
            var other_md_order_date = self.find("[name='md-order-date']").val();
            var other_expected_enter_date = self.find("[name='expected-enter-date']").val();
            var other_md_remark = self.find("[name='md-remark']").val();
            var other_customer_email = self.find("[name='customer-email']").val();
            var chkcomplete = self.find("[name='complete']").is(":checked");
            var chkcancel = self.find("[name='cancel']").is(":checked");
            var other_wh_unshippable_seq = self.find("[name='wh-unshippable-seq']").val();

            var formData = {
                  seq : other_seq
                , cx_flag : other_cx_flag
                , customer_flag : other_customer_flag
                , customer_flag_ori : other_customer_flag_ori
                , order_no : other_order_no
                , md_product_id : other_product_id
                , product_name : other_product_name
                , order_qty : other_order_qty
                , need_qty : other_need_qty
                , md_rep_type : other_md_rep_type
                , md_order_date : other_md_order_date
                , expected_enter_date : other_expected_enter_date
                , md_remark : other_md_remark    
                , customer_email : other_customer_email
            }
            
            if(wh_unshippable_seq == other_wh_unshippable_seq ){
                if(chkcomplete == false){
                    
                    normalList.push(formData)
                    console.log(formData)

                }else{
                    console.log(formData)
                    console.log(chkcomplete)
                    console.log(chkcancel)
                    flag=false;
                }
            }

        });


        length = normalList.length;

        let responseData = {

          normalList : normalList
          , length : length
          , wh_unshippable_seq : wh_unshippable_seq
          , complete : 2
          
        }

        if(!flag){
            alert("미출고 처리가 완료된 상품은 update가 되지않습니다.")
            return false
        }
        console.log(responseData)
        $.ajax({
            url: '/operation/cancelUnshippableDetail',
            type:'POST',
            data: JSON.stringify(responseData),
            dataType: 'json',
            contentType: 'application/json',
            encode: true,
            success:function(response){
                
                if (response.code == '200') {
                    alert("성공");

                    var product = response.object;
                    var wh_unshippable_seq = product.wh_unshippable_seq;
                    var customer_flag = product.customer_flag;
                    console.log(response)
                    $("#unshippableHistory>tr").each(function () {
                        var self = $(this);
                        var other_wh_unshippable_seq = self.find("[name='wh-unshippable-seq']").val();

                        if(wh_unshippable_seq == other_wh_unshippable_seq ){
                            self.find('[name=cancel]').attr('checked',true);
                            self.find('[name=cancel]').attr('disabled', true);
                            $(this).removeClass("row-red");
                            $(this).removeClass("row-white");
                            $(this).addClass("row-green");
                            
                        }
                    });
                } else if (response.code == '403'){
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                }else if (response.code == '500'){
                    alert("서버 에러 : " + response.message)
                }
            }
        });// end getOneTransaction

    }); //end edit btn'

    $(".count-btn").on( "click", function() {
        var self = $(this).parent();
        var count_date = self.find("[name='count-date']").val();

        $.ajax({
            url: '/operation/countUnshippable?count_date=' + count_date,
            type:'POST',
            contentType: 'application/json',
            encode: true,
            success:function(response){
                
                if (response.code == '200') {
                    console.log(response.object)
                    $('#result').html('( ' + count_date + ' 미출고 발생 건수 : ' + response.object + ')');
                    
                    
                } else if (response.code == '403'){
                    alert("새로고침 한 다음 재로그인 후 다시 시도 해주세요.")
                }else if (response.code == '500'){
                    alert("서버 에러 : " + response.message)
                }
            }
        });// end getOneTransaction

    }); //end edit btn'

    
    
});

function pageAction( page){
    console.log(page)
    $("#page").val(page);

    $("#unshippable-list").submit();

}

