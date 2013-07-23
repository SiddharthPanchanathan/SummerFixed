<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" version="4.0" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes" omit-xml-declaration="yes" />
        <xsl:param name="qID"/>
        <xsl:param name="oID"/>
    <xsl:template match="/">
        <xsl:variable name="q_type" select="/questestinterop/item/presentation/response_lid/@nhm_type"/>
	<xsl:variable name="question_src" select="/questestinterop/@type"/>
        <xsl:choose>

	    <xsl:when test="$q_type = 'Fill_Blank_Plain'">
                <xsl:variable name="title3" select="/questestinterop/item/@title"/>
		<xsl:variable name="ans">
			<xsl:apply-templates select="/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_blank_value" mode="serialize"/>
		</xsl:variable>
		<div id="qNo_{$qID}" correctAnswer='{$ans}' type='{$q_type}' class="{$question_src}">
		    <xsl:if test="($question_src = 'wiris')"><div class ='text_wiris'>[Wiris]</div></xsl:if>	
		    <xsl:if test="($question_src = 'qb')"><div class ='text_qb'>[Question Bank]</div></xsl:if>	
                    <div class="question_part">
                        <xsl:for-each select="questestinterop/item/presentation/material">
                            <xsl:if test="count(mattext) > 0">
				<xsl:variable name="question">
					<xsl:apply-templates select="mattext" mode="serialize"/>
				</xsl:variable>
                                <span class="ques_text">
                                    <xsl:value-of select='$question' disable-output-escaping="yes"/>
                                </span>
                            </xsl:if>
                            <xsl:if test="count(matimage) > 0">
                                <xsl:variable name="imgSrc" select="matimage/@nhm_original_file_name"/>
                                <img src="{$imgSrc}" style="vertical-align:middle"/>
                            </xsl:if>
                        </xsl:for-each>
                    </div>
                    <div class="response_part" style="margin-top:10px;">
                        <p>
                            <xsl:value-of select="questestinterop/item/presentation/response_lid/material/mattext" disable-output-escaping="yes"/>
                        </p>
                        <xsl:for-each select="questestinterop/item/presentation/response_lid">
                            <xsl:variable name="pos" select="position()"></xsl:variable>
                            <xsl:variable name="pos1" select="position()-1"></xsl:variable>
                            <xsl:variable name="data_type" select="/questestinterop/item/presentation/response_lid[$pos]/nhm_render_fill/@nhm_expected_data_type"></xsl:variable>
                            <p>
                                <label style="display:block;float:left;margin-top:5px;margin-right:5px">
                                    <xsl:value-of select="/questestinterop/item/resprocessing/respcondition[$pos]/conditionvar/varequal/nhm_blank_name" disable-output-escaping="yes"/>
                                </label>
				        <input type="text" style="display:none;" id="ans_{$oID+$pos1}" name="ans_{$oID+$pos1}"/>
				        <div id="ans_{$oID+$pos1}_html"></div>
				        <button type="button" onclick="popup('ans_{$oID+$pos1}')">Equation editor</button>
                                <xsl:variable name="uPlacement" select="nhm_render_fill/@nhm_unit_placement"/>
                            </p>                
                        </xsl:for-each>
                    </div>
                </div>
            </xsl:when>

            <xsl:when test="($q_type = 'Multiple_Choice') or ($q_type = 'True_False')">
                <xsl:variable name="title3" select="/questestinterop/item/@title"/>
                <xsl:variable name="ans" select="/questestinterop/item/resprocessing/respcondition/conditionvar/varequal"/>
		<div id="qNo_{$qID}" correctAnswer="{$ans}" type='{$q_type}' class="{$question_src}">
		    <xsl:if test="($question_src = 'wiris')"><div class ='text_wiris'>[Wiris]</div></xsl:if>	
		    <xsl:if test="($question_src = 'qb')"><div class ='text_qb'>[Question Bank]</div></xsl:if>	
                    <div class="question_part">
                        <xsl:for-each select="questestinterop/item/presentation/material">
                            <xsl:if test="count(mattext) > 0">
                <xsl:variable name="question">
                    <xsl:apply-templates select="mattext" mode="serialize"/>
                </xsl:variable>
                                <span class="ques_text">
                                    <xsl:value-of select='$question' disable-output-escaping="yes"/>
                                </span>
                            </xsl:if>
                            <xsl:if test="count(matimage) > 0">
                                <span class="empty_space">&#160;</span>
                                <xsl:variable name="imgSrc" select="matimage/@nhm_original_file_name"/>
                                <img src="{$imgSrc}" style="vertical-align:middle"/>&#160;
                            </xsl:if>
                        </xsl:for-each>
                    </div>
                    <div class="response_part" style="margin-top:10px;">
                        <p>
                            <xsl:value-of select="questestinterop/item/presentation/response_lid/material/mattext" disable-output-escaping="yes"/>
                        </p>
                        <ol style="list-style-type:none;padding:10px 0 0 0;margin:0;">
                            <xsl:for-each select="questestinterop/item/presentation/response_lid/render_choice/response_label">
                                <xsl:variable name="choice" select="position()-1"/>
                                <li style="padding:10px 0 0 0;margin:0;">
                                    <xsl:variable name="value" select="@ident" />
                                    <input type="radio" name="radiogroup_{$qID}" id="choice{$choice}" value="{$value}" onclick="populate_radio('{$qID}', '{$value}')"/>
                                    <label for="choice{$choice}">
                                        <xsl:for-each select="material">
                                            <xsl:if test="count(mattext) > 0">
                                                <xsl:value-of select="mattext" disable-output-escaping="yes"/>
                                            </xsl:if>
                                            <xsl:if test="count(matimage) > 0">
                                                <xsl:variable name="imgSrc" select="matimage/@nhm_original_file_name"/>
                                                <img src="{$imgSrc}" style="vertical-align:middle"/>&#160;
                                            </xsl:if>
                                        </xsl:for-each>
                                    </label>
                                </li>                    
                            </xsl:for-each>
                        </ol>
                        <input type="text" style="display:none;" id="ans_{$oID}" name="ans_{$oID}"/>
                    </div>
                </div>
            </xsl:when>

            <xsl:otherwise>
                <span/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

	<xsl:template match="*" mode="serialize">
	  <xsl:text>&lt;</xsl:text>
	  <xsl:value-of select="name()"/>
	  <xsl:text>&gt;</xsl:text>
	  <xsl:apply-templates mode="serialize"/>
	  <xsl:text>&lt;/</xsl:text>
	  <xsl:value-of select="name()"/>
	  <xsl:text>&gt;</xsl:text>
	</xsl:template>
</xsl:stylesheet>