# TravelLog

Android application for recording visits to countries and cities. The first
development milestone displays countries from Supabase; the remaining travel
and account features are not implemented yet.

## Setup

1. Open the project in Android Studio and install the Android SDK requested by
   the project. The Gradle daemon uses JDK 25 as configured in
   `gradle/gradle-daemon-jvm.properties`.
2. Create a Supabase project. Run
   `supabase/migrations/20261110000000_create_countries.sql` once in its SQL
   editor, and ensure the Data API exposes the `public` schema. The migration
   grants only authenticated users read access.
3. Add country records to `public.countries` through the SQL editor or a
   trusted administrative process. The full country and city master datasets
   are not included yet. For a small development-only example:

   ```sql
   insert into public.countries (code, name, name_ja, latitude, longitude)
   values ('JP', 'Japan', '日本', 36.2048, 138.2529);
   ```

4. Create an email/password user in Supabase Auth and confirm the email according
   to your project's Auth settings. In-app registration is not implemented yet.
5. Add the following to your **user-level** `~/.gradle/gradle.properties` (not
   the tracked project file), using the project URL and publishable key from
   Supabase's Connect panel:

   ```properties
   supabaseUrl=https://YOUR_PROJECT.supabase.co
   supabasePublishableKey=YOUR_PUBLISHABLE_KEY
   ```

   Sync/rebuild the project after changing these properties. Only use a
   publishable key (or legacy anon key), never a secret or service-role key.
   Values embedded in an Android app can be extracted, even when supplied via
   a local properties file. Database RLS, not key secrecy, protects data.
6. Run the app and sign in with the account created above. An unset
   configuration displays setup guidance in release builds.

## Debugging without Supabase

Debug builds use stub data automatically when either Supabase setting is
missing. A banner identifies stub mode; the three sample countries are local,
and authentication is bypassed rather than simulated. No Supabase connection
or account is needed. This only covers the current country-list milestone.

If Supabase settings are present but the service is unavailable, explicitly
enable the stub for a debug build:

```shell
./gradlew :app:assembleDebug -PuseStub=true
```

In Android Studio, `useStub=true` can instead be set in the user-level
`~/.gradle/gradle.properties`; sync and rebuild after changing it. Remove the
property to restore automatic selection, or set `useStub=false` to require
Supabase even when its configuration is missing. Configured connection and
authentication failures remain visible; they never silently switch to a stub.
The selection is a build-time setting, not an in-app switch, and release builds
always use Supabase regardless of `useStub`.

Build and run unit tests with:

```shell
./gradlew :app:assembleDebug :app:testDebugUnitTest
```
