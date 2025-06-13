package com.example.webassistant

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when`
import java.io.ByteArrayInputStream
import org.junit.Assert.*

class KnowledgeBaseManagerTest {

    @Mock
    private lateinit var context: Context

    private lateinit var knowledgeBaseManager: KnowledgeBaseManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        knowledgeBaseManager = KnowledgeBaseManager(context)
    }

    @Test
    fun testLoadKnowledgeBase() {
        val jsonString = """
            {
                "providers": {
                    "AT&T": {
                        "port_out_steps": [
                            {"step": 1, "description": "Step 1", "details": "Details 1"},
                            {"step": 2, "description": "Step 2", "details": "Details 2"}
                        ],
                        "porting_info": {
                            "common_issues": [
                                {"issue": "Issue 1", "solution": "Solution 1", "prevention": "Prevention 1"}
                            ]
                        }
                    }
                }
            }
        """.trimIndent()

        `when`(context.assets.open("knowledge_base.json")).thenReturn(ByteArrayInputStream(jsonString.toByteArray()))

        knowledgeBaseManager.loadKnowledgeBase()

        val providerInfo = knowledgeBaseManager.getProviderInfo("AT&T")
        assertNotNull(providerInfo)
        assertEquals("Step 1", providerInfo?.getJSONArray("port_out_steps")?.getJSONObject(0)?.getString("description"))
    }

    @Test
    fun testGetPortingSteps() {
        val jsonString = """
            {
                "providers": {
                    "AT&T": {
                        "port_out_steps": [
                            {"step": 1, "description": "Step 1", "details": "Details 1"},
                            {"step": 2, "description": "Step 2", "details": "Details 2"}
                        ]
                    }
                }
            }
        """.trimIndent()

        `when`(context.assets.open("knowledge_base.json")).thenReturn(ByteArrayInputStream(jsonString.toByteArray()))

        knowledgeBaseManager.loadKnowledgeBase()

        val steps = knowledgeBaseManager.getPortingSteps("AT&T")
        assertEquals(2, steps.size)
        assertEquals("Step 1", steps[0]["description"])
    }

    @Test
    fun testGetCommonIssues() {
        val jsonString = """
            {
                "providers": {
                    "AT&T": {
                        "porting_info": {
                            "common_issues": [
                                {"issue": "Issue 1", "solution": "Solution 1", "prevention": "Prevention 1"}
                            ]
                        }
                    }
                }
            }
        """.trimIndent()

        `when`(context.assets.open("knowledge_base.json")).thenReturn(ByteArrayInputStream(jsonString.toByteArray()))

        knowledgeBaseManager.loadKnowledgeBase()

        val issues = knowledgeBaseManager.getCommonIssues("AT&T")
        assertEquals(1, issues.size)
        assertEquals("Issue 1", issues[0]["issue"])
    }

    @Test
    fun testGetXfinityPortInGuide() {
        val jsonString = """
            {
                "providers": {
                    "AT&T": {
                        "xfinity_port_in_guide": {
                            "prerequisites": ["Prerequisite 1"],
                            "steps": [
                                {"step": 1, "description": "Step 1", "details": "Details 1"}
                            ],
                            "estimated_time": "1 hour",
                            "important_notes": ["Note 1"]
                        }
                    }
                }
            }
        """.trimIndent()

        `when`(context.assets.open("knowledge_base.json")).thenReturn(ByteArrayInputStream(jsonString.toByteArray()))

        knowledgeBaseManager.loadKnowledgeBase()

        val guide = knowledgeBaseManager.getXfinityPortInGuide("AT&T")
        assertEquals("Prerequisite 1", (guide["prerequisites"] as List<String>)[0])
        assertEquals("Step 1", (guide["steps"] as List<Map<String, String>>)[0]["description"])
    }

    @Test
    fun testGetGeneralPortingInfo() {
        val jsonString = """
            {
                "general_porting_info": {
                    "common_terms": {"term1": "definition1"},
                    "best_practices": ["Practice 1"]
                }
            }
        """.trimIndent()

        `when`(context.assets.open("knowledge_base.json")).thenReturn(ByteArrayInputStream(jsonString.toByteArray()))

        knowledgeBaseManager.loadKnowledgeBase()

        val info = knowledgeBaseManager.getGeneralPortingInfo()
        assertEquals("definition1", (info["common_terms"] as Map<String, String>)["term1"])
        assertEquals("Practice 1", (info["best_practices"] as List<String>)[0])
    }

    @Test
    fun testSearchKnowledgeBase() {
        val jsonString = """
            {
                "providers": {
                    "AT&T": {
                        "port_out_steps": [
                            {"step": 1, "description": "Step 1", "details": "Details 1"}
                        ]
                    }
                },
                "general_porting_info": {
                    "common_terms": {"term1": "definition1"}
                }
            }
        """.trimIndent()

        `when`(context.assets.open("knowledge_base.json")).thenReturn(ByteArrayInputStream(jsonString.toByteArray()))

        knowledgeBaseManager.loadKnowledgeBase()

        val results = knowledgeBaseManager.searchKnowledgeBase("Step 1")
        assertTrue(results.containsKey("AT&T"))
    }
} 