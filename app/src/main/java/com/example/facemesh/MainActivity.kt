package com.example.facemesh

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var arFragment: FaceArFragment
    private var faceMeshTexture: Texture? = null
    private var glasses: ArrayList<ModelRenderable> = ArrayList()
    private var faceRegionsRenderable: ModelRenderable? = null

    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var index: Int = 0
    private var changeModel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_next.setOnClickListener {
            changeModel = !changeModel
            index++
            if (index > glasses.size - 1) {
                index = 0
            }
            faceRegionsRenderable = glasses.get(index)
        }

        arFragment = face_fragment as FaceArFragment

        ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(
                this,
                Uri.parse("https://poly.googleusercontent.com/downloads/c/fp/1592560926914800/9i5mmOwt7cu/5ioQlf4aoI5/Glasses_01.gltf"), // gray glasses
                RenderableSource.SourceType.GLTF2)
                .setScale(0.0045f)
                .build())
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(
                this,
                Uri.parse("https://poly.googleusercontent.com/downloads/c/fp/1589852231216668/dCm3NXrMtSr/4qAU-ALSLLg/Sunglasses_01.gltf"), // blue shades
                RenderableSource.SourceType.GLTF2)
                .setScale(0.0045f)
                .build())
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(
                this,
                Uri.parse("https://poly.googleusercontent.com/downloads/c/fp/1592558671942970/0Wsi-ygmiIX/an-7uRcBXOg/Sunglasses.gltf"), // aviator
                RenderableSource.SourceType.GLTF2)
                .setScale(0.0006f)
                .build())
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(
                    this,
                    Uri.parse("https://poly.googleusercontent.com/downloads/c/fp/1592560112932336/7vkAZT8QWF-/3jjMt-6AE7I/model.gltf"), // yellow sunglasses
                    RenderableSource.SourceType.GLTF2)
                .setScale(0.25f)
                .build())
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            if (faceRegionsRenderable != null) {
                sceneView.session
                        ?.getAllTrackables(AugmentedFace::class.java)?.let {
                            for (f in it) {
                                if (!faceNodeMap.containsKey(f)) {
                                    val faceNode = AugmentedFaceNode(f)
                                    faceNode.setParent(scene)
                                    faceNode.faceRegionsRenderable = faceRegionsRenderable
                                    faceNodeMap.put(f, faceNode)
                                } else if (changeModel) {
                                    faceNodeMap.getValue(f).faceRegionsRenderable = faceRegionsRenderable
                                }
                            }
                            changeModel = false
                            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                            val iter = faceNodeMap.entries.iterator()
                            while (iter.hasNext()) {
                                val entry = iter.next()
                                val face = entry.key
                                if (face.trackingState == TrackingState.STOPPED) {
                                    val faceNode = entry.value
                                    faceNode.setParent(null)
                                    iter.remove()
                                }
                            }
                        }
            }
        }
    }
}