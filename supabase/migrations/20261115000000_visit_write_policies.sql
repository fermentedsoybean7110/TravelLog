grant insert (id, user_id, city_id, visited_date, title, memo) on public.visits to authenticated;
grant update (visited_date, title, memo) on public.visits to authenticated;
grant delete on public.visits to authenticated;

create policy "Users can create their own visits"
    on public.visits for insert to authenticated
    with check ((select auth.uid()) = user_id);

create policy "Users can update their own visits"
    on public.visits for update to authenticated
    using ((select auth.uid()) = user_id)
    with check ((select auth.uid()) = user_id);

create policy "Users can delete their own visits"
    on public.visits for delete to authenticated
    using ((select auth.uid()) = user_id);
