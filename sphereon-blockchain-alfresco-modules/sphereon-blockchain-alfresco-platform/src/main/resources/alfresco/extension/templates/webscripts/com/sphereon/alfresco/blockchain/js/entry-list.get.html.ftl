<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
<html>
<head></head>
<body>
<p>
<#attempt>
    <#if result?? && result['blockchainExists']!false>
        Registered on blockchain '${result['blockchainContext']!"unknown"}' at '${result['blockchainAnchorTimeFirst']!"unknown"}', last verification at at '${result['blockchainVerifyTimeLast']!"never"}'.
    <#else>
        Not registered on blockchain '${result['blockchainContext']!"unknown"}'! Last verification at at '${result['blockchainVerifyTimeLast']!"never"}'
    </#if>
    <#recover>
        Could not verify file ${fileName!"unknown"} on the blockchain
</#attempt>
</p>
</body>
</html>