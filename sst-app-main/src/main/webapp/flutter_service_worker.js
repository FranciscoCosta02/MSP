'use strict';
const MANIFEST = 'flutter-app-manifest';
const TEMP = 'flutter-temp-cache';
const CACHE_NAME = 'flutter-app-cache';

const RESOURCES = {"assets/AssetManifest.bin": "092ed983fd850717f687f546d7de9d39",
"assets/AssetManifest.json": "4c54c3370bd00563de80447f4688a49c",
"assets/FontManifest.json": "5a32d4310a6f5d9a6b651e75ba0d7372",
"assets/fonts/MaterialIcons-Regular.otf": "1b1a4cbcac7fb4a6b93176b62d0ddebb",
"assets/images/agora.jpg": "0e8725790660c5d70a2c589e7b4e80dd",
"assets/images/bg-profiletent.png": "23fb5ea729b64f4e837f4e9a79b39412",
"assets/images/biblioteca.jpg": "23bcdb2c3c1e52d4773943cd3483669a",
"assets/images/dcea.jpg": "d2293bec79476ddf628f31fb5bebdcd6",
"assets/images/dcm.jpg": "d80231931b1583f983a5c926a6274dbb",
"assets/images/dcr.jpg": "8fc26bac972f9f1544fa51a22e7baef6",
"assets/images/dcsa.jpg": "4a4cff7e006aad1932e42ed16921ecef",
"assets/images/dct.jpg": "502a95c9ebdcd925c563d08f544155ac",
"assets/images/dec.jpg": "7bbcbc1a19aece57e7c954b6d9c2b58e",
"assets/images/deec.jpg": "6bde2798029f70dc4730faabe8146e02",
"assets/images/defaultProfile.jpg": "ee2df91af452a74f76fcbdca587af88e",
"assets/images/demi.jpg": "e37d1ee4aab182b235bfa3b61ba75083",
"assets/images/departamental.jpg": "8fc26bac972f9f1544fa51a22e7baef6",
"assets/images/desticon.png": "cce4fc7f119583f011623a13ba14352a",
"assets/images/df.jpg": "f9f01fb4b34febdf522a991debb0bb45",
"assets/images/di.jpg": "6f49394555a9d3fa808e3225b8988c10",
"assets/images/dm.jpg": "60677b760fa692fd9f7c1269bf30ca69",
"assets/images/dq.jpg": "f63856cce5529d88a3b191cf93e266ff",
"assets/images/ex7.png": "88cb3896a861221428a4ca2128e7f100",
"assets/images/fundolog.png": "7533b6fe8a4feb5bfbf3628fcfa8416f",
"assets/images/logo-dr.png": "62429a15b40232f110dce4a24d53724d",
"assets/images/logo_nova-st_rgb_vert_mono_positivo.png": "df45a6622bf9297740bb4899e4fcf692",
"assets/images/mapicons/building.png": "8886bb852ecbe225354b453120ff3295",
"assets/images/mapicons/hangar.png": "a6cb690d6e54384fc8b2f7d8b024e438",
"assets/images/mapicons/library.png": "3bacf01c5ae3b3b68d260be0c254ea6d",
"assets/images/mapicons/restaurant.png": "fe93c06158bef028c9a8f1db48ca23f9",
"assets/images/mapicons/school.png": "b4f97ddfb8a30832baa92a45067a80e0",
"assets/images/mapicons/train.png": "ce55d1d4e05bcd9bb7942b67716dea41",
"assets/images/mapicons/waypoint.png": "214c3959a7eec2652c05a5d116032d80",
"assets/images/noimage.jpg": "6f8de53f4487179a97425d854496f5fb",
"assets/images/nova_logo.png": "e419d70759afaa4cd66ddb83d10247e1",
"assets/images/tantofaz.jpg": "46a9ed1dbd81eb9e101a93d80b078fc4",
"assets/images/waypoint.png": "26818e2575f78f4d889a49dccbe06121",
"assets/NOTICES": "c2d82cd64c5a1065b4d472a90d83e942",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "57d849d738900cfd590e9adc7e208250",
"assets/packages/font_awesome_flutter/lib/fonts/fa-brands-400.ttf": "d7791ef376c159f302b8ad90a748d2ab",
"assets/packages/font_awesome_flutter/lib/fonts/fa-regular-400.ttf": "5070443340d1d8cceb516d02c3d6dee7",
"assets/packages/font_awesome_flutter/lib/fonts/fa-solid-900.ttf": "658b490c9da97710b01bd0f8825fce94",
"assets/packages/getwidget/icons/dribble.png": "1e36936e4411f32b0e28fd8335495647",
"assets/packages/getwidget/icons/facebook.png": "293dc099a89c74ae34a028b1ecd2c1f0",
"assets/packages/getwidget/icons/google.png": "596c5544c21e9d6cb02b0768f60f589a",
"assets/packages/getwidget/icons/line.png": "da8d1b531d8189396d68dfcd8cb37a79",
"assets/packages/getwidget/icons/linkedin.png": "822742104a63a720313f6a14d3134f61",
"assets/packages/getwidget/icons/pinterest.png": "d52ccb1e2a8277e4c37b27b234c9f931",
"assets/packages/getwidget/icons/slack.png": "19155b848beeb39c1ffcf743608e2fde",
"assets/packages/getwidget/icons/twitter.png": "caee56343a870ebd76a090642d838139",
"assets/packages/getwidget/icons/wechat.png": "ba10e8b2421bde565e50dfabc202feb7",
"assets/packages/getwidget/icons/whatsapp.png": "30632e569686a4b84cc68169fb9ce2e1",
"assets/packages/getwidget/icons/youtube.png": "1bfda73ab724ad40eb8601f1e7dbc1b9",
"assets/packages/google_places_flutter/images/location.json": "afa33acf2c340246c901718f4efdfccf",
"assets/packages/timezone/data/latest_all.tzf": "871b8008607c14f36bae2388a95fdc8c",
"assets/shaders/ink_sparkle.frag": "f8b80e740d33eb157090be4e995febdf",
"canvaskit/canvaskit.js": "76f7d822f42397160c5dfc69cbc9b2de",
"canvaskit/canvaskit.wasm": "f48eaf57cada79163ec6dec7929486ea",
"canvaskit/chromium/canvaskit.js": "8c8392ce4a4364cbb240aa09b5652e05",
"canvaskit/chromium/canvaskit.wasm": "fc18c3010856029414b70cae1afc5cd9",
"canvaskit/skwasm.js": "1df4d741f441fa1a4d10530ced463ef8",
"canvaskit/skwasm.wasm": "6711032e17bf49924b2b001cef0d3ea3",
"canvaskit/skwasm.worker.js": "19659053a277272607529ef87acf9d8a",
"favicon.png": "5dcef449791fa27946b3d35ad8803796",
"flutter.js": "6b515e434cea20006b3ef1726d2c8894",
"icons/Icon-192.png": "ac9a721a12bbc803b44f645561ecb1e1",
"icons/Icon-512.png": "96e752610906ba2a93c65f8abe1645f1",
"icons/Icon-maskable-192.png": "c457ef57daa1d16f64b27b786ec2ea3c",
"icons/Icon-maskable-512.png": "301a7604d45b3e739efc881eb04896ea",
"index.html": "4ce166b24571f9620c840ad83cedb0ad",
"/": "4ce166b24571f9620c840ad83cedb0ad",
"main.dart.js": "1a3589ef46d45cb6e3187526a4c7fa52",
"manifest.json": "4b1c4d03dcbbafe29040e60d4df28c19",
"version.json": "b4a099de83fba1220c741364bc6b4330"};
// The application shell files that are downloaded before a service worker can
// start.
const CORE = ["main.dart.js",
"index.html",
"assets/AssetManifest.json",
"assets/FontManifest.json"];

// During install, the TEMP cache is populated with the application shell files.
self.addEventListener("install", (event) => {
  self.skipWaiting();
  return event.waitUntil(
    caches.open(TEMP).then((cache) => {
      return cache.addAll(
        CORE.map((value) => new Request(value, {'cache': 'reload'})));
    })
  );
});
// During activate, the cache is populated with the temp files downloaded in
// install. If this service worker is upgrading from one with a saved
// MANIFEST, then use this to retain unchanged resource files.
self.addEventListener("activate", function(event) {
  return event.waitUntil(async function() {
    try {
      var contentCache = await caches.open(CACHE_NAME);
      var tempCache = await caches.open(TEMP);
      var manifestCache = await caches.open(MANIFEST);
      var manifest = await manifestCache.match('manifest');
      // When there is no prior manifest, clear the entire cache.
      if (!manifest) {
        await caches.delete(CACHE_NAME);
        contentCache = await caches.open(CACHE_NAME);
        for (var request of await tempCache.keys()) {
          var response = await tempCache.match(request);
          await contentCache.put(request, response);
        }
        await caches.delete(TEMP);
        // Save the manifest to make future upgrades efficient.
        await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
        // Claim client to enable caching on first launch
        self.clients.claim();
        return;
      }
      var oldManifest = await manifest.json();
      var origin = self.location.origin;
      for (var request of await contentCache.keys()) {
        var key = request.url.substring(origin.length + 1);
        if (key == "") {
          key = "/";
        }
        // If a resource from the old manifest is not in the new cache, or if
        // the MD5 sum has changed, delete it. Otherwise the resource is left
        // in the cache and can be reused by the new service worker.
        if (!RESOURCES[key] || RESOURCES[key] != oldManifest[key]) {
          await contentCache.delete(request);
        }
      }
      // Populate the cache with the app shell TEMP files, potentially overwriting
      // cache files preserved above.
      for (var request of await tempCache.keys()) {
        var response = await tempCache.match(request);
        await contentCache.put(request, response);
      }
      await caches.delete(TEMP);
      // Save the manifest to make future upgrades efficient.
      await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
      // Claim client to enable caching on first launch
      self.clients.claim();
      return;
    } catch (err) {
      // On an unhandled exception the state of the cache cannot be guaranteed.
      console.error('Failed to upgrade service worker: ' + err);
      await caches.delete(CACHE_NAME);
      await caches.delete(TEMP);
      await caches.delete(MANIFEST);
    }
  }());
});
// The fetch handler redirects requests for RESOURCE files to the service
// worker cache.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== 'GET') {
    return;
  }
  var origin = self.location.origin;
  var key = event.request.url.substring(origin.length + 1);
  // Redirect URLs to the index.html
  if (key.indexOf('?v=') != -1) {
    key = key.split('?v=')[0];
  }
  if (event.request.url == origin || event.request.url.startsWith(origin + '/#') || key == '') {
    key = '/';
  }
  // If the URL is not the RESOURCE list then return to signal that the
  // browser should take over.
  if (!RESOURCES[key]) {
    return;
  }
  // If the URL is the index.html, perform an online-first request.
  if (key == '/') {
    return onlineFirst(event);
  }
  event.respondWith(caches.open(CACHE_NAME)
    .then((cache) =>  {
      return cache.match(event.request).then((response) => {
        // Either respond with the cached resource, or perform a fetch and
        // lazily populate the cache only if the resource was successfully fetched.
        return response || fetch(event.request).then((response) => {
          if (response && Boolean(response.ok)) {
            cache.put(event.request, response.clone());
          }
          return response;
        });
      })
    })
  );
});
self.addEventListener('message', (event) => {
  // SkipWaiting can be used to immediately activate a waiting service worker.
  // This will also require a page refresh triggered by the main worker.
  if (event.data === 'skipWaiting') {
    self.skipWaiting();
    return;
  }
  if (event.data === 'downloadOffline') {
    downloadOffline();
    return;
  }
});
// Download offline will check the RESOURCES for all files not in the cache
// and populate them.
async function downloadOffline() {
  var resources = [];
  var contentCache = await caches.open(CACHE_NAME);
  var currentContent = {};
  for (var request of await contentCache.keys()) {
    var key = request.url.substring(origin.length + 1);
    if (key == "") {
      key = "/";
    }
    currentContent[key] = true;
  }
  for (var resourceKey of Object.keys(RESOURCES)) {
    if (!currentContent[resourceKey]) {
      resources.push(resourceKey);
    }
  }
  return contentCache.addAll(resources);
}
// Attempt to download the resource online before falling back to
// the offline cache.
function onlineFirst(event) {
  return event.respondWith(
    fetch(event.request).then((response) => {
      return caches.open(CACHE_NAME).then((cache) => {
        cache.put(event.request, response.clone());
        return response;
      });
    }).catch((error) => {
      return caches.open(CACHE_NAME).then((cache) => {
        return cache.match(event.request).then((response) => {
          if (response != null) {
            return response;
          }
          throw error;
        });
      });
    })
  );
}
