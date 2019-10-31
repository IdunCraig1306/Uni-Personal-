package model;

public enum Category {
	ACTION{
			@Override
			public String description() {
			
				return "Action";
				}
			},
	COMEDY{
			@Override
			public String description() {
				
				return "Comedy";
				}
			},
	HORROR{
			@Override
			public String description() {
					
				return "Horror";
				}
			},
	DRAMA{
			@Override
			public String description() {
						
				return "Drama";
				}
			},
	THRILLER{
			@Override
			public String description() {
							
				return "Thriller";
				}
			},			
	ROMANCE{
			@Override
			public String description() {
					
				return "Romance";
				}
			};

		 public abstract String description();
}
