alter table customer_items_for_sale
    add constraint UK_1mutv6cqowgpg7ty2tld2syfu unique (items_for_sale_id);

alter table customer_items_owned
    add constraint UK_n15ec6kvut4fl48f2oubslf85 unique (items_owned_id);

alter table item_bids
    add constraint UK_psog9xm3r3yp95u13uy2g08nq unique (bids_id);

alter table bid
    add constraint FKrbsu6wgwsg2loyayorebqj9qf
        foreign key (customer_id)
            references customer (id);

alter table bid
    add constraint FK9qeb5m2ef85uovk0nhso3vi48
        foreign key (item_id)
            references item (id);

alter table customer_items_for_sale
    add constraint FKnbnt19cxjltk9x1pm6h1n3vwn
        foreign key (items_for_sale_id)
            references item (id);

alter table customer_items_for_sale
    add constraint FK3t21lmuwjbhvdajfgqpai4qrc
        foreign key (customer_id)
            references customer (id);

alter table customer_items_owned
    add constraint FKio7346vpr9o4v4ilii35v2opx
        foreign key (items_owned_id)
            references item (id);

alter table customer_items_owned
    add constraint FKbut5dcam2v10x7857ljp4ka90
        foreign key (customer_id)
            references customer (id);

alter table item
    add constraint FKlfn4kb7sv12ce1fs4exnx84rw
        foreign key (buyer_id)
            references customer (id);

alter table item_bids
    add constraint FKklgujbwjem97xqlpdrfg5h12w
        foreign key (bids_id)
            references bid (id);

alter table item_bids
    add constraint FKso8o3nefx5ijuptq6l4x2f8xo
        foreign key (item_id)
            references item (id);