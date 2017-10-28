<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<HTML>
<BODY>
<xsl:apply-templates/>
</BODY>
</HTML>
</xsl:template> 

<!-- default pattern for elements: copy -->
<xsl:template match="*">
<xsl:copy>
<xsl:apply-templates/>
</xsl:copy>
</xsl:template>

<!-- default pattern for attributes: copy -->
<xsl:template match="@*">
<xsl:copy/>
</xsl:template>    

<xsl:template match="ParsingLog">
<H2>Parse Log</H2>
<FONT color="red">
<PRE>
<xsl:apply-templates/>
</PRE>
</FONT>
</xsl:template>   

<xsl:template match="ExecLog">
<H2>Execution Log</H2>
<FONT color="red">
<PRE>
<xsl:apply-templates/>
</PRE>
</FONT>
</xsl:template>   

<xsl:template match="query">
<H2>Query (parsing time = <xsl:value-of select="@parsingTime"/>)</H2>
<PRE>
<xsl:apply-templates/> 
</PRE>
</xsl:template>

<xsl:template match="kweelt-result">
Parsing time: <xsl:value-of select="@parsingTime"/>
Execution time: <xsl:value-of select="@parsingTime"/>
<H2>Result</H2>
<xsl:apply-templates/>
</xsl:template>  

<xsl:template match="data">
<PRE>
<xsl:apply-templates/>
</PRE>
</xsl:template>  


</xsl:stylesheet>
