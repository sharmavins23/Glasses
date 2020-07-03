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
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    lateinit var arFragment: FaceArFragment
    private var faceMeshTexture: Texture? = null
    private var glasses: ArrayList<ModelRenderable> = ArrayList()
    private var faceRegionsRenderable: ModelRenderable? = null
    private lateinit var allObjects: JSONArray

    private var fileServerURL: String = "http://vinsdev.serveousercontent.com/glasses" // Fileserver URL from localhost

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

        // Request all assets and create all models
        requestAssets()

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

    // Function to request all asset objects and create models out of them
    private fun requestAssets() {
        var requestTask: String =
            NetworkAsyncCall(this@MainActivity, fileServerURL, RequestHandler.GET).execute().get()!! // This should never be null
        allObjects = ParseJson(requestTask).getJSONArray("assets")

        for (i in 0 until allObjects.length()) { // Iterate through all objects and add
            createModel(Asset(allObjects.getJSONObject(i).toString())) // Add to the glasses array the new ModelRenderable
        }
    }

    // Function to create a new model and add it to the glasses list
    private fun createModel(asset: Asset) {
        ModelRenderable.builder()
            .setSource(arFragment.context, RenderableSource.builder().setSource(
                arFragment.context,
                Uri.parse(asset.asset_url),
                RenderableSource.SourceType.GLTF2)
                .setScale(asset.default_scalar)
                .build())
            .build()
            .thenAccept {
                glasses.add(it)
                faceRegionsRenderable = it
                it.isShadowCaster = false
                it.isShadowReceiver = false
            }
    }
}