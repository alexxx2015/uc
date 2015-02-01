<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
	xmlns:err="http://www.w3.org/2005/xqt-errors" exclude-result-prefixes="xs xdt err fn">

	<xsl:output method="xml" indent="no" />

	<xsl:template match="/">
		<xsl:element name="Policy">
			<xsl:attribute name="Subject">
				<xsl:for-each select="//Block">
					<xsl:if test="@genus-name='subject'">
						<xsl:value-of select="Label" />
					</xsl:if>
				</xsl:for-each>
			</xsl:attribute>
			<xsl:element name="Obligation">
				<xsl:for-each select="//Block">
					<xsl:if test="@genus-name='Policy'">
						<xsl:for-each select="//Sockets/BlockConnector">
							<xsl:if test="@label='Obligation'">
								<xsl:variable name="obl" select="@con-block-id"></xsl:variable>
								<xsl:for-each select="//Block">
									<xsl:if test="@id=$obl">
										<xsl:element name="{@genus-name}">
											<xsl:variable name="numConnectors" select="Sockets/@num-sockets"></xsl:variable>
											<xsl:call-template name="renderObBlocks">
												<xsl:with-param name="numConn" select="$numConnectors" />
											</xsl:call-template>
										</xsl:element>
									</xsl:if>

								</xsl:for-each>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="renderObBlocks">
		<xsl:param name="numConn" />

		<!--xsl:if test="count($numConn) > 0" -->

		<xsl:for-each select="Sockets/BlockConnector">
			<xsl:variable name="gid" select="@con-block-id"></xsl:variable>
			<xsl:variable name="socklabel" select="@label"></xsl:variable>
			<xsl:for-each select="//Block">
				<xsl:if test="@id=$gid">
					<xsl:choose>
						<xsl:when test="$socklabel='name'">
							<xsl:element name="name">
								<xsl:value-of select="@genus-name" />
								<xsl:if test="Params">


									<xsl:element name="Params">
										<xsl:for-each select="//Param">
											<xsl:variable name="vStatus" select="@status" />
											<xsl:if test="$vStatus='true'">
												<xsl:element name="Param">
													<xsl:attribute name="name">
																<xsl:value-of select="@name" />
															</xsl:attribute>
													<xsl:attribute name="class">
																<xsl:value-of select="@class" />
															</xsl:attribute>
													<xsl:attribute name="policyType">
																<xsl:value-of select="@policyType" />
															</xsl:attribute>
													<xsl:attribute name="value">
																<xsl:value-of select="@value" />
															</xsl:attribute>
												</xsl:element>
											</xsl:if>

										</xsl:for-each>
									</xsl:element>


									<xsl:call-template name="renderObBlocks" />
								</xsl:if>
								<xsl:call-template name="renderObBlocks" />
							</xsl:element>
						</xsl:when>
						<xsl:when test="$socklabel='type'">
							<xsl:element name="type">
								<xsl:value-of select="@genus-name" />
								<xsl:call-template name="renderObBlocks"></xsl:call-template>
							</xsl:element>
						</xsl:when>

						<xsl:when test="@genus-name='number'">
							<xsl:element name="num">
								<xsl:attribute name="value"><xsl:value-of
									select="Label/text()" /></xsl:attribute>
								<xsl:call-template name="renderObBlocks"></xsl:call-template>
							</xsl:element>
						</xsl:when>

						<xsl:when test="@genus-name='string'">
							<xsl:element name="{Label/text()}">
								<xsl:call-template name="renderObBlocks"></xsl:call-template>
							</xsl:element>
						</xsl:when>

						<xsl:otherwise>
							<xsl:element name="{@genus-name}">
								<xsl:call-template name="renderObBlocks"></xsl:call-template>
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>

				</xsl:if>
			</xsl:for-each>

		</xsl:for-each>

	</xsl:template>

</xsl:stylesheet>
