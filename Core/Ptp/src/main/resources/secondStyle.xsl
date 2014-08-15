<?xml version="1.0" encoding="UTF-8" ?>

<!-- New document created with EditiX at Mon Dec 03 19:20:47 CET 2012 -->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" indent="yes" />
	<xsl:param name="pNames" select="'|Location|BoxSize|'" />

	<!-- default: copy everything using the identity transform -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!-- retain only params; remove diagram relatd detail, whetever is left -->
	<xsl:template match="Params">
		<xsl:element name="Params">
			<xsl:apply-templates select="Param" />
		</xsl:element>
	</xsl:template>

	<!-- bring "Params" one level higher: out of "name" tag -->
	<xsl:template match="name">
		<xsl:copy>
			<xsl:apply-templates select="child::node()[not(self::Params)]" />
		</xsl:copy>
		<xsl:apply-templates select="Params" />
	</xsl:template>


	<!-- convert name and type from elements to attributes -->
	<xsl:template match="action">
		<Action>
			<xsl:for-each select="name">
				<xsl:attribute name="{name()}">
          <xsl:value-of select="text()" />
        </xsl:attribute>
			</xsl:for-each>


			<xsl:for-each select="type">
				<xsl:attribute name="{name()}">
          <xsl:value-of select="text()" />
        </xsl:attribute>
			</xsl:for-each>

			<!-- while converting them into attributes, params was lost; copy it here -->
			<xsl:copy-of select="Params" />
			<xsl:apply-templates select="@*|node()" />
		</Action>

	</xsl:template>


	<!-- remove duplicate name and type -->
	<!-- first remove the text of the nodes, because if you directly delete 
		the nodes and not their text, the text mysteriously lingers on. i am not 
		xslt expert! -->
	<xsl:template match="name/text()|type/text()">
		<xsl:apply-templates select="@*|node()" />
	</xsl:template>

	<!-- now that the text is removed, delete empty nodes -->
	<xsl:template match="name|type">
		<xsl:apply-templates select="@*|node()" />
	</xsl:template>


</xsl:stylesheet>


