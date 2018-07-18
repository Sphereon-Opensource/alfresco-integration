<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
<#attempt>
   <#if result['blockchainExists']!false>
    File '${fileName!"unknown"}' was first registered on blockchain '${result['blockchainContext']!"unknown"}' at '${result['blockchainAnchorTimeFirst']!"unknown"}', last verification at '${result['blockchainVerifyTimeLast']!"never"}'.
   <#else>
    File '${fileName!"unknown"}' was not registered on blockchain '${result['blockchainContext']!"unknown"}'! Last verification at '${result['blockchainVerifyTimeLast']!"never"}'
   </#if>
   <#recover>
    Could not verify file ${fileName!"unknown"} on the blockchain
</#attempt>