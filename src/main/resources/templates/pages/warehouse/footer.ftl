<#escape x as x?html>
...
<#macro page title>
<html>
  <head>
    <title>${title}</title>
    <style>

    html {
      height: 100%;
    }

    body {
      min-height: 100%;
      position: relative;
      padding-bottom: 87px;
    }

    footer {
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      background-color: #343a40;
      color: white;
      text-align: center;
      clear: both;
    }

    .footer-content {
      padding: 30px;
      font-size: 18px;
      margin-bottom: 0;
    }
    </style>
  </head>
  <footer class="footer-copy">
    <p class="footer-content">&copy; <script>document.write(new Date().getFullYear())</script> HANPOOM. All Rights Reserved.</p>
  </footer>
</html>
</#macro>
...
</#escape>
