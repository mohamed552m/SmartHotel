Add-Type -AssemblyName System.Drawing

function Patch-Image([string]$backupPath, [string]$targetPath, [object[]]$ops) {
    $stream = [System.IO.File]::OpenRead($backupPath)
    try {
        $image = [System.Drawing.Image]::FromStream($stream)
        $bitmap = New-Object System.Drawing.Bitmap $image
        $image.Dispose()
    } finally {
        $stream.Dispose()
    }

    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

    foreach ($op in $ops) {
        $dest = New-Object System.Drawing.Rectangle $op.dx, $op.dy, $op.dw, $op.dh
        $src = New-Object System.Drawing.Rectangle $op.sx, $op.sy, $op.sw, $op.sh
        $graphics.DrawImage($bitmap, $dest, $src, [System.Drawing.GraphicsUnit]::Pixel)
    }

    $graphics.Dispose()

    $tempPath = "$targetPath.tmp.png"
    $bitmap.Save($tempPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bitmap.Dispose()
    Move-Item $tempPath $targetPath -Force
}

$root = 'D:\myprojects\Hotel\app\src\main\res\drawable-nodpi'

Patch-Image `
    (Join-Path $root 'login_reference_backup.png') `
    (Join-Path $root 'login_reference.png') `
    @(
        @{ sx = 480; sy = 500; sw = 190; sh = 108; dx = 690; dy = 500; dw = 190; dh = 108 }
        @{ sx = 772; sy = 610; sw = 82; sh = 82; dx = 870; dy = 610; dw = 82; dh = 82 }
        @{ sx = 342; sy = 684; sw = 120; sh = 120; dx = 486; dy = 684; dw = 120; dh = 120 }
    )

Patch-Image `
    (Join-Path $root 'signup_reference_backup.png') `
    (Join-Path $root 'signup_reference.png') `
    @(
        @{ sx = 420; sy = 528; sw = 240; sh = 118; dx = 680; dy = 528; dw = 240; dh = 118 }
        @{ sx = 808; sy = 592; sw = 72; sh = 72; dx = 900; dy = 592; dw = 72; dh = 72 }
        @{ sx = 332; sy = 742; sw = 126; sh = 126; dx = 488; dy = 742; dw = 126; dh = 126 }
    )
