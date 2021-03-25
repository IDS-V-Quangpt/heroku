$(function(){
  
  var updateStyleSelectField = function(element) {
    var current = element.val();
    if (current != '') {
      element.css('color',"#222");
    } else {
      element.css('color','#b4bcc3');
    }
  };

  // Progress bar
  $(".progressbar").each(function(){
    var data = $(this).data("progress") - 0;
    var done = $("<div>").addClass("progress-done");
    var empty = $("<div>").addClass("progress-empty");
    for (var i = 1; i <= 20; i++) {
      if(i/20 < data) {
        done.clone(true).appendTo($(this));
      } else {
        empty.clone(true).appendTo($(this));
      }
    }
  });

  $("#toggleSideNavigation").on("click", function(){
    $(".side-navigation, .side-navigation-container").toggleClass("hide");
  });

  $("#toggleGlobalMenu").on("click", function(){
    $(".navbar-nav, .global-menu-toggle").toggleClass("show");
  });


  var largeTooltipContainer = $("<div>").addClass("large-tooltip-container");
  largeTooltipContainer.append("<h3>").append("<p>").append($("<span>").addClass("mdi mdi-close-circle"));

  $(document).on("click", ".large-tooltip-container .mdi-close-circle", function(){
    $(this).parent(".large-tooltip-container").remove();
  });
  
  $(".info").each(function(){
    var header = $(this).text()
    var body = $(this).data("info").split(',');
    $(this).append($("<span>").addClass("mdi mdi-information help-button"));
    $(this).children(".help-button").on("click", function(){
      $(this).after(largeTooltipContainer);
      largeTooltipContainer.children("h3").text(header);
      //largeTooltipContainer.children("p").text(body);
      $.each(body, function(index, value){
        largeTooltipContainer.append("<p>");
        largeTooltipContainer.children("p")[index].innerText = value;
      })
    });
  });

  // Mobile view
  var windowSize = $(window).width();
  if(windowSize <= 840) {
    $(".side-navigation, .side-navigation-container").addClass("hide");
  }
});