<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
<#attempt>
   <#if result['blockchain-exists']!false>
    File '${fileName!"unknown"}' was first registered on blockchain '${result['blockchain-context']!"unknown"}' at '${result['blockchain-anchor-time-first']!"unknown"}', last verification at '${result['blockchain-verify-time-last']!"never"}'.
   <#else>
    File '${fileName!"unknown"}' was not registered on blockchain '${result['blockchain-context']!"unknown"}'! Last verification at '${result['blockchain-verify-time-last']!"never"}'
   </#if>
   <#recover>
    Could not verify file ${fileName!"unknown"} on the blockchain
</#attempt>