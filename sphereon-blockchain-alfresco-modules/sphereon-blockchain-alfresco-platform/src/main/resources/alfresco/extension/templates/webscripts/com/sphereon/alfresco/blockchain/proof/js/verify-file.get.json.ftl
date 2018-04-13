<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
{
<#list result?keys as key>
   "${key}":<#if !result[key]?has_content><#elseif result[key]?is_boolean>${result[key]?string('true', 'false')}<#elseif result[key]?is_string>"${result[key]}"<#else>"${result[key]?string}"</#if>,
</#list>
}