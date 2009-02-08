<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>
<!-- JWorkSheet HTML stylesheet 
<title>Large event table</title>
<copyright>PPone(c)2007</copyright>
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
<!-- Parameters: -->    
<xsl:param name="Title"    select="'jWorkSheet'"/>
<xsl:param name="DateFrom" select="'0000-00-00'"/>    
<xsl:param name="DateTo"   select="'9999-12-31'"/>    
<xsl:param name="BaseUrl"  select="'.'"/>
<xsl:param name="WorkingHours" select="'8.0'"/>    
<xsl:param name="ColorOfPrivateProject"  select="'5DA158'"/>
<xsl:param name="ColorOfFinishedProject" select="'7B784A'"/>
<xsl:param name="JWSName"     select="'jWorkSheet'"/>
<xsl:param name="JWSHomePage" select="'http://jworksheet.ponec.net/'"/>
<xsl:param name="ReportCSS"   select="'styles/style.css'"/>

<!-- Labels: -->
<xsl:param name="labelCreated"    select="'Created'"/>
<xsl:param name="labelDateFrom"   select="'Date from'"/>
<xsl:param name="labelDateTo"     select="'Date to'"/>
<xsl:param name="labelDate"       select="'Date'"/>
<xsl:param name="labelTime"       select="'Time'"/>
<xsl:param name="labelPeriod"     select="'Period'"/>
<xsl:param name="labelProject"    select="'Project'"/>
<xsl:param name="labelTask"       select="'Task'"/>
<xsl:param name="labelDescription" select="'Description'"/>
    
<xsl:template match="/">
<html lang="en"><head>
<title><xsl:value-of select="$Title"/></title>
<meta name="Generator" content="jWorkSheet" />
<base><xsl:attribute name="href"><xsl:value-of select="$BaseUrl" /></xsl:attribute></base>
<link rel="stylesheet" type="text/css" href="styles/style.css" />
<link rel="stylesheet" type="text/css"><xsl:attribute name="href"><xsl:value-of select="$ReportCSS" /></xsl:attribute></link> 

</head>
<body>
<h2 style="margin-bottom:0px;"><xsl:value-of select="$Title"/></h2>
<div style="margin-bottom:20px;"><xsl:value-of select="$labelCreated"/>: <xsl:value-of select="translate(/body/Created,'T','&nbsp;&nbsp;')"/></div>

<table class="filter" cellspacing="0" border="0">
    <tr><td><xsl:value-of select="$labelDateFrom"/>: </td><td><xsl:value-of select="$DateFrom" /></td></tr>
    <tr><td><xsl:value-of select="$labelDateTo"/>:   </td><td><xsl:value-of select="$DateTo"   /></td></tr>
</table>    

<table border="1" cellspacing="1" class="events">
        <tr>
        <th align="left"><xsl:value-of select="$labelDate"/></th>
        <th align="right"><xsl:value-of select="$labelTime"/></th>
        <th align="right" title="[second]"><xsl:value-of select="$labelPeriod"/></th>
        <th align="left"><xsl:value-of select="$labelProject"/></th>
        <th align="left"><xsl:value-of select="$labelTask"/></th>
        <th align="left"><xsl:value-of select="$labelDescription"/></th>
        </tr>
<xsl:for-each select="body/Day">
<xsl:sort select="Date"/>

    <xsl:if test="translate(Date,'-','')>=translate($DateFrom,'-','') and translate(Date,'-','')&lt;=translate($DateTo,'-','')">
	<xsl:for-each select="Event">
           <xsl:if test="number(Period) != 0 ">
	        <xsl:variable name="ProjectID" select="ProjectID"/>
	        <xsl:variable name="TaskID" select="TaskID"/>
		<tr>
		<td align="left"><xsl:value-of select="../Date"/></td>
		<td align="right"><xsl:value-of select="Time"/></td>
		<td align="right"><xsl:value-of select="Period"/></td>
		<td align="left"><xsl:value-of select="/body/Project[ID = $ProjectID]/Description"/>&nbsp;<span class="projId">[<xsl:value-of select="ProjectID"/>]</span></td>
		<td align="left"><xsl:value-of select="/body/Project[ID = $ProjectID]/Task[ID = $TaskID]/Description"/>&nbsp;<span class="taskId">[<xsl:value-of select="TaskID"/>]</span></td>
		<td align="left"><xsl:value-of select="Description"/>&nbsp;</td>
		</tr>
            </xsl:if>   
	</xsl:for-each>
    </xsl:if>   
</xsl:for-each>
</table>


<hr />
<div class="footer">Powered by 
<a><xsl:attribute name="href"><xsl:value-of select="$JWSHomePage" /></xsl:attribute><xsl:value-of select="$JWSName" /></a> 
 version <xsl:value-of select="/body/Version" />
</div>

</body>
</html>
</xsl:template>
</xsl:stylesheet>
