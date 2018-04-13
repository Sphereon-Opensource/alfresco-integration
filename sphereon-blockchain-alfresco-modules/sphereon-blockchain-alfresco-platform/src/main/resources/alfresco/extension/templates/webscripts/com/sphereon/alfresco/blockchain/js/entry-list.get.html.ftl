<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
<html>
<head></head>
<body>
<p>
<#attempt>
    <#if result?? && result['blockchain-exists']!false>
        Registered on blockchain '${result['blockchain-context']!"unknown"}' at '${result['blockchain-anchor-time-first']!"unknown"}', last verification at at '${result['blockchain-verify-time-last']!"never"}'.
    <#else>
        Not registered on blockchain '${result['blockchain-context']!"unknown"}'! Last verification at at '${result['blockchain-verify-time-last']!"never"}'
    </#if>
    <#recover>
        Could not verify file ${fileName!"unknown"} on the blockchain
</#attempt>
</p>
</body>
</html>