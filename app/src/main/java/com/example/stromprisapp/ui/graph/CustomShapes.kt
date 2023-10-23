package com.example.stromprisapp.ui.graph

import androidx.compose.foundation.shape.GenericShape

//https://foso.github.io/Jetpack-Compose-Playground/cookbook/how_to_create_custom_shape/
val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
}