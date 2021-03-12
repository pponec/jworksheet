<?xml version="1.0" encoding="UTF-8"?>
<!-- JWorkSheet HTML stylesheet 
<title>Weekly Summaries</title>
<copyright>(c) B3Partners 2010</copyright>
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:date="http://exslt.org/dates-and-times" xmlns:xalan="http://xml.apache.org/xalan" extension-element-prefixes="date">
	<!-- Parameters: -->
	<xsl:param name="Title" select="'jWorkSheet'"/>
	<xsl:param name="DateFrom" select="'0000-00-00'"/>
	<xsl:param name="DateTo" select="'9999-12-31'"/>
	<xsl:param name="BaseUrl" select="'.'"/>
	<xsl:param name="WorkingHours" select="'8.0'"/>
	<xsl:param name="ColorOfPrivateProject" select="'5DA158'"/>
	<xsl:param name="ColorOfFinishedProject" select="'7B784A'"/>
	<xsl:param name="JWSName" select="'jWorkSheet'"/>
	<xsl:param name="JWSHomePage" select="'http://jworksheet.ponec.net/'"/>
	<xsl:param name="ReportCSS" select="'styles/style.css'"/>
	<!-- Labels: -->
	<xsl:param name="labelCreated" select="'Created'"/>
	<xsl:param name="labelDateFrom" select="'Date from'"/>
	<xsl:param name="labelDateTo" select="'Date to'"/>
	<xsl:param name="labelDate" select="'Date'"/>
	<xsl:param name="labelTime" select="'Time'"/>
	<xsl:param name="labelPeriod" select="'Period'"/>
	<xsl:param name="labelProject" select="'Project'"/>
	<xsl:param name="labelTask" select="'Task'"/>
	<xsl:param name="labelDescription" select="'Description'"/>
	<xsl:param name="labelDefault" select="'Default'"/>
	<xsl:param name="labelFinished" select="'Finished'"/>
	<xsl:param name="labelPrivate" select="'Private'"/>
	<xsl:param name="labelTasks" select="'Tasks'"/>
	<xsl:param name="labelID" select="'ID'"/>
	<xsl:template match="/">
		<html lang="en">
			<head>
				<title>
					<xsl:value-of select="$Title"/>
					<xsl:text> for </xsl:text>
					<xsl:value-of select="/body/Username"/>
				</title>
				<meta name="Generator" content="jWorkSheet"/>
				<base>
					<xsl:attribute name="href"><xsl:value-of select="$BaseUrl"/></xsl:attribute>
				</base>
				<link rel="stylesheet" type="text/css" href="styles/style.css"/>
				<link rel="stylesheet" type="text/css">
					<xsl:attribute name="href"><xsl:value-of select="$ReportCSS"/></xsl:attribute>
				</link>
			</head>
			<body>
				<h2 style="margin-bottom:0px;">
					<xsl:value-of select="$Title"/>
					<xsl:text> for </xsl:text>
					<xsl:value-of select="/body/Username"/>
				</h2>
				<div style="margin-bottom:20px;">
					<xsl:value-of select="$labelCreated"/>: <xsl:value-of select="translate(/body/Created,'T','&#160;&#160;')"/>
				</div>
				<table cellspacing="0" class="filter">
					<tr>
						<td>
							<xsl:value-of select="$labelDateFrom"/>
							<xsl:text>: </xsl:text>
						</td>
						<td>
							<xsl:value-of select="$DateFrom"/>
						</td>
					</tr>
					<tr>
						<td>
							<xsl:value-of select="$labelDateTo"/>
							<xsl:text>: </xsl:text>
						</td>
						<td>
							<xsl:value-of select="$DateTo"/>
						</td>
					</tr>
				</table>
				<xsl:variable name="ExtendedDaysTemp">
					<xsl:for-each select="body/Day">
						<xsl:sort select="Date"/>
						<xsl:if test="translate(Date,'-','')>=translate($DateFrom,'-','') and translate(Date,'-','')&lt;=translate($DateTo,'-','')">
							<xsl:copy>
								<xsl:attribute name="year"><xsl:value-of select="date:year(Date)"/></xsl:attribute>
								<xsl:attribute name="week"><xsl:value-of select="date:week-in-year(Date)"/></xsl:attribute>
								<xsl:copy-of select="Date"/>
								<xsl:copy-of select="Event"/>
							</xsl:copy>
						</xsl:if>
					</xsl:for-each>
				</xsl:variable>
				<xsl:variable name="ExtendedDays" select="xalan:nodeset($ExtendedDaysTemp)"/>
				<xsl:variable name="ExtendedWeeksTemp">
					<xsl:for-each select="$ExtendedDays/Day[not(@year = preceding-sibling::Day/@year)]">
						<xsl:sort select="Date"/>
						<xsl:variable name="Year" select="@year"/>
						<xsl:for-each select="$ExtendedDays/Day[not(@week = preceding-sibling::Day/@week and @year = $Year)]">
							<xsl:sort select="Date"/>
							<xsl:variable name="Week" select="@week"/>
							<xsl:element name="Week">
								<xsl:attribute name="year"><xsl:value-of select="@year"/></xsl:attribute>
								<xsl:attribute name="week"><xsl:value-of select="@week"/></xsl:attribute>
								<xsl:for-each select="$ExtendedDays/Day[@year = $Year and @week = $Week]/Event">
									<xsl:copy-of select="."/>
								</xsl:for-each>
							</xsl:element>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:variable>
				<xsl:variable name="ExtendedWeeks" select="xalan:nodeset($ExtendedWeeksTemp)"/>
				<br/>
				<table cellspacing="0" class="events border">
					<tr>
						<th align="left">
							<xsl:value-of select="'Week'"/>
						</th>
						<th align="left">
							<xsl:value-of select="'Username'"/>
						</th>
						<th align="left">
							<xsl:value-of select="$labelProject"/>
						</th>
						<th align="left">
							<xsl:value-of select="$labelTask"/>
						</th>
						<th align="right" title="[hours]">
							<xsl:value-of select="$labelPeriod"/>
						</th>
					</tr>
					<xsl:for-each select="$ExtendedWeeks/Week/Event[not(ProjectID = preceding-sibling::Event/ProjectID) and ProjectID and TaskID]">
						<xsl:sort select="ProjectID"/>
						<xsl:variable name="ProjectID" select="ProjectID"/>
						<xsl:variable name="TaskID" select="TaskID"/>
						<tr>
							<td align="right">
								<xsl:value-of select="../@year"/>
								<xsl:if test="string-length(../@week)=1">
									<xsl:text>0</xsl:text>
								</xsl:if>
								<xsl:value-of select="../@week"/>
							</td>
							<td align="right">
								<xsl:value-of select="/body/Username"/>
							</td>
							<td align="left">
								<xsl:value-of select="/body/Project[ID = $ProjectID]/Description"/>&#160;<span class="projId">[<xsl:value-of select="ProjectID"/>]</span>
							</td>
							<td align="left">
								<xsl:value-of select="/body/Project[ID = $ProjectID]/Task[ID = $TaskID]/Description"/>&#160;<span class="taskId">[<xsl:value-of select="TaskID"/>]</span>
							</td>
							<td align="right">
								<xsl:value-of select="format-number(sum(../Event[ProjectID = $ProjectID and TaskID = $TaskID]/Period) div 60, '####0.00')"/>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="$ExtendedWeeks/Week/Event[not(ProjectID = preceding-sibling::Event/ProjectID) and ProjectID and not (TaskID)]">
						<xsl:sort select="ProjectID"/>
						<xsl:variable name="ProjectID" select="ProjectID"/>
						<tr>
							<td align="right">
								<xsl:value-of select="../@year"/>
								<xsl:if test="string-length(../@week)=1">
									<xsl:text>0</xsl:text>
								</xsl:if>
								<xsl:value-of select="../@week"/>
							</td>
							<td align="right">
								<xsl:value-of select="/body/Username"/>
							</td>
							<td align="left">
								<xsl:value-of select="/body/Project[ID = $ProjectID]/Description"/>&#160;<span class="projId">[<xsl:value-of select="ProjectID"/>]</span>
							</td>
							<td align="left">
								<xsl:text>no tasks</xsl:text>
							</td>
							<td align="right">
								<xsl:value-of select="format-number(sum(../Event[ProjectID = $ProjectID]/Period) div 60, '####0.00')"/>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<hr/>
				<div class="footer">
					<xsl:text>Powered by </xsl:text>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="$JWSHomePage"/></xsl:attribute>
						<xsl:value-of select="$JWSName"/>
					</a>
					<xsl:text>version </xsl:text>
					<xsl:value-of select="/body/Version"/>
					<img src="styles/logo16.png"/>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
