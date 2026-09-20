package com.deniscerri.ytdl.receiver

import android.content.ComponentName
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.service.chooser.ChooserTarget
import android.service.chooser.ChooserTargetService
import androidx.annotation.RequiresApi
import com.deniscerri.ytdl.R

@RequiresApi(api = Build.VERSION_CODES.N)
class ZettaChooserTargetService : ChooserTargetService() {

    override fun onGetChooserTargets(
        targetActivityName: ComponentName?,
        candidateQuery: IntentFilter?
    ): MutableList<ChooserTarget> {
        val targets = mutableListOf<ChooserTarget>()

        val icon = Icon.createWithResource(this, R.drawable.ic_launcher_foreground_large)
        val componentName = ComponentName(this, ShareActivity::class.java)

        targets.add(
            ChooserTarget(
                "Download with Zetta",
                icon,
                0.5f, // relevance score (0-1), higher = more prominent
                componentName,
                null // extras
            )
        )

        return targets
    }
}
