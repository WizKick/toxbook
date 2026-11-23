$src = "C:\ToxBook\app\src\main\assets\poisons.json"
$dst = "C:\ToxBook\app\src\main\assets\poisons.json.new"
$data = Get-Content $src -Raw -Encoding UTF8 | ConvertFrom-Json

$caustiques = @("Eau de Javel (hypochlorite de sodium)","Ammoniaque (hydroxyde d'ammonium)","Formol (formaldéhyde 35–40 %)","Eau oxygénée 35 % (peroxyde d'hydrogène concentré)")

$rules = @(
  @{ t = "MORT";       k = "mort|mortel|mortelle|létal|fatale|fatal|décès" },
  @{ t = "VOMI";       k = "vomissement" },
  @{ t = "CARDIAQUE";  k = "cardiaque|arythmie|bradycardie|tachycardie|asystolie|bloc cardiaque|QRS" },
  @{ t = "NEURO";      k = "confusion|encéphalopathie|neurologique|ataxie|somnolence|ébriété" },
  @{ t = "COMA";       k = "coma" },
  @{ t = "CONVULS";    k = "convulsion|spasme|fasciculation" },
  @{ t = "HEMO";       k = "hémorragie|hémolyse|hémorragique|saignement|méléna|ecchymose" },
  @{ t = "FOIE";       k = "hépatique|hépatite|foie" },
  @{ t = "REIN";       k = "rénale|rénal|oligurie|rein" },
  @{ t = "RESPIR";     k = "respiratoire|apnée|dyspnée|asphyxie|bronchospasme|stridor" },
  @{ t = "CAUSTIQUE";  k = "caustiqu" },
  @{ t = "CECITE";     k = "cécité|optique|vision" },
  @{ t = "HALLU";      k = "hallucination|délire|délirant" },
  @{ t = "ACIDOSE";    k = "acidose" },
  @{ t = "HYPOGLY";    k = "hypoglycémie" },
  @{ t = "DIGESTIF";   k = "diarrhée|douleur abdominale|gastro-entérite|dysphagie" },
  @{ t = "INFLAM";     k = "inflammation|exothermique|flamme|combustion|projection" }
)

foreach ($o in $data) {
  $txt = ($o.e + " " + $o.s)
  $tg = @()
  foreach ($r in $rules) {
    if ($txt -match $r.k) { $tg += $r.t }
  }
  if ($caustiques -contains $o.n) { $tg += "CAUSTIQUE" }
  $tg = $tg | Select-Object -Unique
  if ($tg.Count -eq 0) { $tg = @("DIVERS") }
  $o | Add-Member -NotePropertyName tg -NotePropertyValue $tg -Force

  $di = 3
  $no = $o.no
  if ($no -match "goût neutre|inodore|incolore|sans odeur") { $di += 2 }
  if ($no -match "goût sucré") { $di += 1 }
  if ($no -match "odeur") { $di -= 1 }
  if ($o.t -match "jour") { $di += 1 }
  if ($o.t -match "immédiat|secondes|minutes") { $di -= 1 }
  if ($o.e -match "caustique") { $di -= 1 }
  if ($o.c -match "Ménager|Industriel") { $di -= 1 }
  if ($di -lt 1) { $di = 1 }
  if ($di -gt 5) { $di = 5 }
  $o | Add-Member -NotePropertyName di -NotePropertyValue $di -Force

  $o | Add-Member -NotePropertyName st -NotePropertyValue $false -Force
}

$out = $data | ConvertTo-Json -Depth 5
$enc = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($dst, $out, $enc)
"Fiches traitees : " + $data.Count