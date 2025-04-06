package com.parser

import com.model.Graph

interface IGraphParser {
    fun parse(input: String): Graph
}