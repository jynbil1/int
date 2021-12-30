<#escape x as x?html>
...
<#macro page title>
  <html>
    <head>
      <title>${title}</title>
    </head>
    

    <#nested>
    
    <div class="topnav">
    <#--  <a class="active" href="/signIn">Home</a>  -->
    <a href="/">Home</a>
    <a href="/searchProductPage">Search</a>
  
    <div class="dropdown">
      <button class="dropbtn">Arrival 
        <i class="arrow down"></i>
      </button>
      <div class="dropdown-content">
        <a href="/arrivalProductPage">Arrival</a>
        <a href="/arrivalProductHistoryPage">Arrival History</a>
      </div>
    </div>
  
    <div class="dropdown">
      <button class="dropbtn">Enter 
        <i class="arrow down"></i>
      </button>
      <div class="dropdown-content">
        <a href="/inProductPage">Enter</a>
        <a href="/inProductHistoryPage">Enter History</a>
      </div>
    </div>
  
    <div class="dropdown">
      <button class="dropbtn">Move 
        <i class="arrow down"></i>
      </button>
      <div class="dropdown-content">
        <a href="/moveProductPage">Move</a>
        <a href="/dMoveProductPage">Direct Move</a>
        <a href="/moveProductHistoryPage">Move History</a>
      </div>
    </div>
  
    <div class="dropdown">
      <button class="dropbtn">Loss 
        <i class="arrow down"></i>
      </button>
      <div class="dropdown-content">
        <a href="/lossProductPage">Loss</a>
        <a href="/lossProductHistory">Loss History</a>
      </div>
    </div>
  
    <div class="dropdown">
      <button class="dropbtn">Out 
        <i class="arrow down"></i>
      </button>
      <div class="dropdown-content">
        <a href="/shippedOrderPage">Shipped Order</a>
        <a href="/outProductPage">OutProduct History</a>
        <a href="/operationAreaStockPage">Operation Area Stock</a>
      </div>
    </div>

    <div class="dropdown">
      <button class="dropbtn">발주
        <i class="arrow down"></i>
      </button>
      <div class="dropdown-content">
        <a href="/orderProductPage">발주</a>
        <a href="/manageOrderProduct">발주 상품 관리</a>
      </div>
    </div>

    <#--  <a class="active" href="/orderProductPage">test</a>  -->
    
      
    <div id="logout-button">
      <#if user_id??>
        <p id="username">User [ ${user_id} ]</p>
        <a href='/signOut' style="color: #FF6347;">로그아웃</a>
      <#else >
        <a href="/signIn">Sign In</a>
      </#if>
    </div>

    <a href="/manager-list" onclick="savepagecount()">Product-Manager</a>

        
  </div>

  <script>
    function savepagecount() {
        
        var cntpage = 1;
        localStorage.setItem("cntpage", cntpage);
    }
  </script>
    
  </html>
</#macro>
...
</#escape>
